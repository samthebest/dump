 public class ConfigurationReader
    {
        private readonly Config _config;

        public ConfigurationReader()
        {
            _config = new Config();   
        }

        public void BuildSQLConfig()
        {
            var sqlConfig = SqlConfigReader.ReadSQLConfig();
            _config.SqlConnectionString = sqlConfig.ConnectionString;
        }

        public void BuildAzureConfiguration()
        {
            var azureConfig = AzureConfigurationReader.ReadConfigFromAzure();
            _config.ServicePrincipalId = azureConfig.ServicePrincipalId;
            _config.TenantId = azureConfig.TenantId;
        }

        public Config GetConfig()
        {
            return _config;
        }
    }
    
     class Program
    {
        public static void Main()
        {
            var configurationReader = new ConfigurationReader();
            configurationReader.BuildAzureConfiguration();
            configurationReader.BuildSQLConfig();

            var config = configurationReader.GetConfig();
            InitialiseSQLConnection(config);
        }

        private static void InitialiseSQLConnection(Config config)
        {
            SQLInitialisationStuffs.Initialise(config.SqlConnectionString);
        }
    }
