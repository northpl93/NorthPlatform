package pl.north93.zgame.api.global.component.impl;

import java.lang.instrument.Instrumentation;

import sun.management.Agent;

class ClassTweaker
{
    static class NorthPlatformTweakerAgent extends Agent
    {
        public static void agentmain(final String string, final Instrumentation instrument)
        {
            System.out.println("Agent connected.");
        }
    }
}
