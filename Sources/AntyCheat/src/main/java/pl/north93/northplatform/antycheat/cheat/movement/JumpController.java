package pl.north93.northplatform.antycheat.cheat.movement;


import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import pl.north93.northplatform.antycheat.analysis.FalsePositiveProbability;
import pl.north93.northplatform.antycheat.analysis.SingleAnalysisResult;
import pl.north93.northplatform.antycheat.event.impl.ClientMoveTimelineEvent;
import pl.north93.northplatform.antycheat.event.impl.VelocityAppliedTimelineEvent;
import pl.north93.northplatform.antycheat.timeline.DataKey;
import pl.north93.northplatform.antycheat.timeline.PlayerData;
import pl.north93.northplatform.antycheat.timeline.PlayerTickInfo;
import pl.north93.northplatform.antycheat.timeline.virtual.VirtualPlayer;
import pl.north93.northplatform.antycheat.utils.AntyCheatMath;
import pl.north93.northplatform.antycheat.utils.DistanceUtils;
import pl.north93.northplatform.antycheat.utils.EntityUtils;
import pl.north93.northplatform.antycheat.utils.PlayerUtils;
import pl.north93.northplatform.antycheat.utils.location.RichEntityLocation;

public class JumpController
{
    private static final String                  RISING_IN_FALL_STAGE        = "Player is rising in FALL stage.";
    private static final String                  MAX_HEIGHT_EXCEEDED         = "Player exceeded max jump height.";
    private static final String                  HORIZONTAL_RISING_EXCEEDED  = "Player exceeded rising horizontal distance.";
    private static final String                  HORIZONTAL_FALLING_EXCEEDED = "Player exceeded falling horizontal distance.";
    private static final String                  INCONSISTENCY_START_VECTOR  = "Player ignored their start vector.";
    private static final double                  MIN_HEIGHT_TO_EXCEED        = 0.26;
    private static final DataKey<JumpController> KEY                         = new DataKey<>("jumpController", JumpController::new);

    public static JumpController get(final PlayerData playerData)
    {
        return playerData.get(KEY);
    }

    private final PlayerData playerData;

    /** Aktualny jump stage gracza */
    private JumpStage jumpStage = JumpStage.GROUND;
    public enum JumpStage
    {
        GROUND,
        RISE,
        FALL
    }

    /**Liczba pakietów w których gracz spadał, jest używane dla rzadkich false-positive
     * gdy czasami rejestrujemy że gracz powinien zacząć spadać, a tak naprawdę zaczyna skok.
     * @see #tryEnterFallingStage()
     */
    private int startFallingPackets;

    /** Lokacja gdzie entity zaczęło skok lub upadek */
    private RichEntityLocation startLocation;

    /** Informacje o ticku w którym zaczęto wznoszenie lub upadek */
    private PlayerTickInfo startTickInfo;

    /** Startowe velocity, moze byc ustawione przez zewnetrzne zrodlo lub obliczone w #getStartVelocity() */
    private Vector startVelocity;

    // konstruktor
    public JumpController(final PlayerData playerData)
    {
        this.playerData = playerData;
    }

    // metoda wejsciowa analizujaca event
    public SingleAnalysisResult handleMovement(final PlayerTickInfo tickInfo, final ClientMoveTimelineEvent event)
    {
        final SingleAnalysisResult singleAnalysisResult = SingleAnalysisResult.create();

        if (event.isFromOnGround() && ! event.isToOnGround())
        {
            //Bukkit.broadcastMessage("wystartowano z ziemi");
            this.tearOffGround(tickInfo, event, singleAnalysisResult);
        }
        else if (! event.isFromOnGround() && ! event.isToOnGround())
        {
            this.flyHandle(tickInfo, event, singleAnalysisResult);
        }
        else if (event.isToOnGround())
        {
            this.handleLanding(event);
        }

        if (tickInfo.isShortAfterTeleport() || tickInfo.isShortAfterSpawn())
        {
            return SingleAnalysisResult.EMPTY;
        }

        if (! singleAnalysisResult.isEmpty())
        {
            // todo klientowi czasami odwala i wyglada jakby chodzil szybciej niz moze
            PlayerUtils.updateProperties(event.getOwner());
        }

        return singleAnalysisResult;
    }

    public void forceReset()
    {
        this.jumpStage = JumpStage.GROUND;
        this.startFallingPackets = 0;
        this.startVelocity = null;
        this.startTickInfo = null;
        this.startLocation = null;
    }

    public void changeVelocity(final VelocityAppliedTimelineEvent newVelocity)
    {
        if (this.jumpStage == JumpStage.GROUND)
        {
            //Bukkit.broadcastMessage("velocity zmienione na ziemi");
            this.startVelocity = newVelocity.getVelocity();
            return;
        }

        this.forceReset();
        this.startVelocity = newVelocity.getVelocity();
        //Bukkit.broadcastMessage("velocity zmienione w powietrzu + reset jump controllera");
    }

    // gracz odrywa sie od ziemi. Skacze lub spada.
    private void tearOffGround(final PlayerTickInfo tickInfo, final ClientMoveTimelineEvent event, final SingleAnalysisResult result)
    {
        if (this.jumpStage == JumpStage.GROUND)
        {
            // rejestrujemy pierwsza pozycje startowa, nawet jak klient wysle kilka pakietow
            this.startLocation = event.getFrom();
            this.startTickInfo = tickInfo;
        }

        final double heightDiff = event.getTo().getY() - event.getFrom().getY();
        if (heightDiff >= 0)
        {
            this.jumpStage = JumpStage.RISE;
        }
        else
        {
            //Bukkit.broadcastMessage("fall in tearOffGround heightDiff:" + heightDiff);
            //this.jumpStage = JumpStage.FALL;
            this.tryEnterFallingStage();
        }

        //Bukkit.broadcastMessage("tearOffGround new stage: " + this.jumpStage);
    }

    // gracz jest w powietrzu
    private void flyHandle(final PlayerTickInfo tickInfo, final ClientMoveTimelineEvent event, final SingleAnalysisResult result)
    {
        final RichEntityLocation toLocation = event.getTo();
        final RichEntityLocation fromLocation = event.getFrom();

        if (toLocation.getY() >= fromLocation.getY()) // unosi sie
        {
            if (this.jumpStage == JumpStage.FALL)
            {
                // gracz podczas gdy powinien opadac zaczal sie unosic
                final FalsePositiveProbability risingInFallStageFalsePositive;
                if (toLocation.isStands() || toLocation.getDistanceToGround() <= 1.25)
                {
                    risingInFallStageFalsePositive = FalsePositiveProbability.HIGH;
                }
                else
                {
                    risingInFallStageFalsePositive = FalsePositiveProbability.MEDIUM;
                }

                result.addViolation(MovementViolation.SURVIVAL_FLY, RISING_IN_FALL_STAGE, risingInFallStageFalsePositive);
            }
            else if (this.jumpStage == JumpStage.RISE)
            {
                // na razie wszystko jest dobrze. teraz weryfikujemy czy gracz może skakać na taką wysokość.
                this.verifyPlayerRisingStage(event, result);

                // resetujemy ilosc pakietów o opadaniu. Bo się w końcu unosimy.
                this.startFallingPackets = 0;
            }
            else
            {
                // Gracz zaczyna się unosić gdy jest w stage GROUND, oznacza to problemy z flagą onGround.
                // Teoretycznie możnaby triggerowac jakies violation, ale zajmuje sie tym OnGroundManipulationChecker
                this.startLocation = fromLocation;
                this.startTickInfo = tickInfo;
                this.jumpStage = JumpStage.RISE;
            }
        }
        else if (toLocation.getY() < fromLocation.getY()) // opada
        {
            if (this.jumpStage == JumpStage.FALL)
            {
                // ok, sprawdzamy wysokosc/szybkosc
                this.verifyPlayerFallingStage(event, result);
            }
            else if (this.jumpStage == JumpStage.RISE)
            {
                // Gracz podczas lotu w górę zaczął opadać, to oznacza że mógł osiągnąć maksymalną wysokość skoku.
                if (this.tryEnterFallingStage())
                {
                    this.startLocation = fromLocation;
                    this.startTickInfo = tickInfo;
                }
                //Bukkit.broadcastMessage("fall in flyHandle/jumpStage==rise");
                //this.jumpStage = JumpStage.FALL;
            }
            else
            {
                // Gracz zaczyna opadać gdy jest w stage GROUND, oznacza to problemy z flagą onGround.
                // Teoretycznie możnaby triggerowac jakies violation, ale zajmuje sie tym OnGroundManipulationChecker
                if (this.tryEnterFallingStage())
                {
                    this.startLocation = fromLocation;
                    this.startTickInfo = tickInfo;
                }
                //Bukkit.broadcastMessage("fall in flyHandle/jumpStage==ground");
                //this.jumpStage = JumpStage.FALL;
            }
        }
        //Bukkit.broadcastMessage("flyHandle new stage: " + this.jumpStage);
    }

    // gracz laduje na ziemi
    private void handleLanding(final ClientMoveTimelineEvent event)
    {
        if (this.jumpStage == JumpStage.GROUND)
        {
            return;
        }

        this.forceReset();
        //Bukkit.broadcastMessage("handleLanding new stage: " + this.jumpStage);
    }

    // próbujemy rozpoczac opadanie, ale mozemy to zrobic dopiero po kilku pakietach dla pewnosci
    // bo inaczej lapiemy false-positive podczas energicznego skakania.
    private boolean tryEnterFallingStage()
    {
        if (this.startFallingPackets <= 2)
        {
            this.startFallingPackets++;
            return false;
        }

        this.startFallingPackets = 0;
        this.startVelocity = null;
        this.jumpStage = JumpStage.FALL;
        return true;
    }

    // weryfikuje etap unoszenia się gracza
    private void verifyPlayerRisingStage(final ClientMoveTimelineEvent event, final SingleAnalysisResult result)
    {
        final RichEntityLocation to = event.getTo();
        final Vector startVector = this.getStartVelocity(event.getOwner(), event.getTo());

        final double jumpHeight = to.getY() - this.startLocation.getY();
        final double maxHeight = EntityUtils.maxHeightByStartVelocity(startVector.getY());

        //Bukkit.broadcastMessage("startVector.getY:" + startVector.getY());
        //Bukkit.broadcastMessage("jumpHeight: " + jumpHeight + " maxHeight: " + maxHeight);

        // sprawdzamy czy i o ile gracz przekroczył maksymalną wysokość
        final double maxHeightExceeded = jumpHeight - maxHeight;
        if (maxHeightExceeded > MIN_HEIGHT_TO_EXCEED && maxHeightExceeded <= 1)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, MAX_HEIGHT_EXCEEDED, FalsePositiveProbability.HIGH);
        }
        else if (maxHeightExceeded > 1 && maxHeightExceeded <= 4)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, MAX_HEIGHT_EXCEEDED, FalsePositiveProbability.MEDIUM);
        }
        else if (maxHeightExceeded > 4)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, MAX_HEIGHT_EXCEEDED, FalsePositiveProbability.LOW);
        }

        final double horizontalDistanceFromStart = DistanceUtils.xzDistance(this.startLocation, to);
        final double expectedHorizontalDistance = this.calculateMaxRisingHorizontalDistance(startVector, maxHeight);

        // sprawdzamy czy gracz sie wysunął się zbyt daleko na osiach xz
        final double horizontalExceeded = horizontalDistanceFromStart - expectedHorizontalDistance;
        if (horizontalExceeded > 1)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, HORIZONTAL_RISING_EXCEEDED, FalsePositiveProbability.LOW);
        }
        else if (horizontalExceeded > 0.5)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, HORIZONTAL_RISING_EXCEEDED, FalsePositiveProbability.MEDIUM);
        }
        else if (horizontalExceeded > 0.25)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, HORIZONTAL_RISING_EXCEEDED, FalsePositiveProbability.HIGH);
        }

        // porównujemy wektor ruchu w tym evencie do początkowego wektora ruchu
        final Vector currentMovementVector = event.getFrom().vectorToOther(to);
        if (currentMovementVector.length() > 0.5 && to.getDistanceToGround() >= 0.5)
        {
            final double cosineSimilarity = AntyCheatMath.cosineSimilarity(startVector.clone().normalize(), currentMovementVector.normalize());
            //Bukkit.broadcastMessage(format("currentMovementVector={0}", currentMovementVector));
            //Bukkit.broadcastMessage(format("startVector={0}", startVector));
            if (cosineSimilarity <= 0.1 && maxHeightExceeded > 0.5)
            {
                // dodatkowo zwiekszamy wymagania zeby uniknac bolesnych false-positives
                result.addViolation(MovementViolation.SURVIVAL_FLY, INCONSISTENCY_START_VECTOR, FalsePositiveProbability.MEDIUM);
            }
            else if (cosineSimilarity < 0.15)
            {
                //Bukkit.broadcastMessage(currentMovementVector.toString());
                //Bukkit.broadcastMessage("" + cosineSimilarity);
                result.addViolation(MovementViolation.SURVIVAL_FLY, INCONSISTENCY_START_VECTOR, FalsePositiveProbability.HIGH);
            }
            //Bukkit.broadcastMessage(ChatColor.RED + "v:" + cosineSimilarity + " maxHeightExceeded:" + maxHeightExceeded);
        }
    }

    private void verifyPlayerFallingStage(final ClientMoveTimelineEvent event, final SingleAnalysisResult result)
    {
        final RichEntityLocation to = event.getTo();
        final Vector startVector = this.getStartVelocity(event.getOwner(), to);

        final double fallenDistance = this.startLocation.getY() - to.getY();

        final double horizontalDistanceFromStart = DistanceUtils.xzDistance(this.startLocation, to);
        final double expectedHorizontalDistance = this.calculateMaxFallingHorizontalDistance(startVector, fallenDistance);

        final double horizontalExceeded = horizontalDistanceFromStart - expectedHorizontalDistance;
        if (horizontalExceeded > 4)
        {
            //Bukkit.broadcastMessage("start: " + this.startLocation.toBukkit());
            //Bukkit.broadcastMessage("to: " + to.toBukkit());
            result.addViolation(MovementViolation.SURVIVAL_FLY, HORIZONTAL_FALLING_EXCEEDED, FalsePositiveProbability.LOW);
        }
        else if (horizontalExceeded > 2)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, HORIZONTAL_FALLING_EXCEEDED, FalsePositiveProbability.MEDIUM);
        }
        else if (horizontalExceeded > 0.5)
        {
            result.addViolation(MovementViolation.SURVIVAL_FLY, HORIZONTAL_FALLING_EXCEEDED, FalsePositiveProbability.HIGH);
        }
        //Bukkit.broadcastMessage("FALL EXPECTED:" + expectedHorizontalDistance + " DIST:" + horizontalDistanceFromStart);
    }

    // staramy sie obliczyc wektor z jakim wystartował gracz
    private Vector getStartVelocity(final Player player, final RichEntityLocation targetLocation)
    {
        // uznajemy że gracz zawsze może osiągnąć wysokość normalnego skoku.
        // Bez tego czasami łapiemy dziwne false-positives przy intensywnym skakaniu z piruetami.
        final double normalJumpVelocity = this.calculateJumpVelocity(player);
        if (this.startVelocity != null)
        {
            final double newY = Math.max(normalJumpVelocity, this.startVelocity.getY());
            return new Vector(this.startVelocity.getX(), newY, this.startVelocity.getZ());
        }
        else
        {
            final double vectorMultiplier = 0.25;
            final Vector vector = this.startLocation.vectorToOther(targetLocation);

            // obliczamy wektor startowy z poczatkowego ruchu gracza i go zapisujemy
            return this.startVelocity = new Vector(vector.getX() * vectorMultiplier, normalJumpVelocity, vector.getZ() * vectorMultiplier);
        }
    }

    private double calculateJumpVelocity(final Player player)
    {
        double velocity = 0.42;
        final PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.JUMP);
        if (potionEffect != null)
        {
            velocity += (potionEffect.getAmplifier() + 1) * 0.1;
        }

        return velocity;
    }

    private double calculateMaxRisingHorizontalDistance(final Vector startVelocity, final double maxHeight)
    {
        final double heightBonus = maxHeight / (maxHeight + 1);
        final double normalJump = this.getBaseJumpDistance() + heightBonus; // policzone z dupy

        final double maxDistanceX = EntityUtils.maxDistanceByStartVelocity(Math.abs(startVelocity.getX()));
        final double maxDistanceZ = EntityUtils.maxDistanceByStartVelocity(Math.abs(startVelocity.getZ()));

        final double distanceFromVelocity = Math.sqrt(maxDistanceX * maxDistanceX + maxDistanceZ * maxDistanceZ);
        final double adjustedFromVelocity = distanceFromVelocity * 0.75;

        return Math.max(normalJump, adjustedFromVelocity);
    }

    private double calculateMaxFallingHorizontalDistance(final Vector startVelocity, final double fallDistance)
    {
        final Vector lookingDirection = this.startLocation.getDirection().multiply(0.1);
        final double velX = Math.max(Math.abs(lookingDirection.getX()), Math.abs(startVelocity.getX()));
        final double velZ = Math.max(Math.abs(lookingDirection.getZ()), Math.abs(startVelocity.getZ()));

        final double maxDistanceX = EntityUtils.maxDistanceByStartVelocity(velX);
        final double maxDistanceZ = EntityUtils.maxDistanceByStartVelocity(velZ);

        final double vectorCrossProductXZ = Math.sqrt(maxDistanceX * maxDistanceX + maxDistanceZ * maxDistanceZ);
        return vectorCrossProductXZ + fallDistance * 0.5;
    }

    private double getBaseJumpDistance()
    {
        final VirtualPlayer virtualPlayer = VirtualPlayer.get(this.playerData);

        final boolean sprintingWhileStarted = virtualPlayer.isSprinting();
        final double walkSpeed = this.startTickInfo.getMovementSpeed();
        //Bukkit.broadcastMessage("movSpeed: " + properties.getMovementSpeed());

        return sprintingWhileStarted ? walkSpeed * 15.5 : walkSpeed * 10;
    }
}
