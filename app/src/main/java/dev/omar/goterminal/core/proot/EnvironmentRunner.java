package dev.omar.goterminal.core.proot;

import java.io.IOException;

public interface EnvironmentRunner {
    Process startEnvironment() throws IOException;
}