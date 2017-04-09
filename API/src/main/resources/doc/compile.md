API
===
Informacje o kompilowaniu głównego API

Wymagane biblioteki wypakowane do .jara
---------------------------------------
* bson-2.4.2
* commons-io-2.5
* commons-lang3-3.4
* commons-pool2-2.4.2
* ea-agent-loader-1.0.2
* fastutil-7.0.12
* gson-2.3.1
* guava-18
* javassist-3.19-GA
* lettuce-4.3.1
* mongo-java-driver-3.4.2
* msgpack-core-0.8.11
* netty-buffer-4.1.6.Final
* netty-codec-4.1.5.Final
* netty-common-4.1.6.Final
* netty-handler-4.1.6.Final
* netty-resolver-4.1.6.Final
* netty-transport-4.1.6.Final
* reflections-0.9.10
* rxjava-1.2.1
* slf4j-api-1.7.22
* slf4j-jdk14-1.7.22
* snakeyaml-1.14
* vecmath-1.5.2

Plik manifest
-------------
W jarze powinien znaleźć się manifest mający główną klasę ustawioną na
`pl.north93.zgame.api.standalone.StandaloneApiCore`

Instrumentation agent
---------------------
Java Agent wymagany do modyfikowania kodu podczas działania aplikacji
(wstrzykiwanie zależności)
Plik jar musi znaleźć się w jarze API pod nazwą InstrumentationAgent.jar.
Zostanie wtedy wypakowany przy pierwszym uruchomieniu do katalogu gdzie API trzyma konfiguracje
pod nazwą NorthPlatformInstrumentation.jar
* /plugins/API - na Bukkit i Bungee
* w tym samym katalogu co API.jar - jako aplikacja zewnętrzna

Jeśli nie możesz ustawić IDE tak żeby robiło jar w jarze (lub kompilujesz przez mavena) to możesz
ręcznie umieścić plik NorthPlatformInstrumentation.jar w odpowiednim miejscu (zgodnie z listą wyżej)

API.InstrumentationAgent
========================
Nie wymaga żadnych bibliotek. Musi zawierać w sobie plik manifest z katalogu
`API/Instrumentation Agent/META-INF`
żeby mógł poprawnie działać jako agent.

RestfulApi
==========
Wymagane biblioteki
-------------------
* javax.servlet-api-3.1.0
* Wszystkie jetty-
* slf4j-api-1.7.13
* spark-core-2.5.5
* Wszystkie websocket-