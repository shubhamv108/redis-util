package client.redis.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Env {
    private static Config defaultConf = ConfigFactory.load();
    private static Config envConf     = ConfigFactory.systemEnvironment();
    public  static Config config      = defaultConf.withFallback(envConf);
}