using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data.SqlClient;

namespace TestApp1
{
    class Program
    {
        static void Main(string[] args)
        {
            // Read environment variables
            string controllerIP = Environment.GetEnvironmentVariable("CONTROLLER_IP");
            string controllerPassword = Environment.GetEnvironmentVariable("CONTROLLER_PASSWORD");
            string controllerKey = Environment.GetEnvironmentVariable("CONTROLLER_ENCRYPTIONKEY");
            string miCR=Environment.GetEnvironmentVariable("MI_NAME");
            string arcNamespace=Environment.GetEnvironmentVariable("ARC_NAMESPACE");

            string connString = $@"Server = {controllerIP}; Database = controller; User ID = controldb-rw-user; Password = {controllerPassword};";
            string targetFile = $@"/config/namespaces/{arcNamespace}/scaledsets/{miCR}/containers/fluentbit/files/fluentbit-out-elasticsearch.conf";
            string sourceFile = @"/workspaces/otel-hackathon/Arc-dotnet-file-delivery-injector/Injector/files/elasticsearch_otel.conf";

            // Read in text file from drive
            String sourceText = ReadFile(sourceFile);
            // Console.WriteLine(sourceText);

            try
            {
                using (SqlConnection conn = new SqlConnection(connString))
                {
                    SqlDataReader dr;
                    conn.Open();

                    // Open Symmetric Key for this session
                    string query = $@"OPEN SYMMETRIC KEY ControllerDbSymmetricKey DECRYPTION BY PASSWORD = '{controllerKey}';";
                    SqlCommand cmd = new SqlCommand(query, conn);
                    cmd.ExecuteNonQuery(); 
                    
                    // - - - - - - - - - - - - - -
                    // INSERT into SQL Server
                    // - - - - - - - - - - - - - -
                    query = $@"DECLARE @sample_data varchar(max);
                               SET @sample_data = N'{sourceText}';

                               DECLARE @targetFile varchar(max);
                               SET @targetFile = N'{targetFile}';

                               UPDATE [dbo].[Files] SET [data] = EncryptByKey(Key_GUID('ControllerDbSymmetricKey'), @sample_data)
                               WHERE [file_path] = @targetFile;";

                    cmd = new SqlCommand(query, conn);
                    dr = cmd.ExecuteReader();
                    dr.Close();

                    // - - - - - - - - - - - - - -
                    // Read from SQL Server
                    // - - - - - - - - - - - - - -

                    // Target File
                    query = $@"SELECT file_path, secret_decrypted FROM (
                        	    select *, convert(varchar(max), DECRYPTBYKEY(data)) as 'secret_decrypted' from controller.dbo.Files
	                        ) AS T
                            WHERE file_path = '{targetFile}'
                            ORDER BY created_time DESC";
                    cmd = new SqlCommand(query, conn);
                    dr = cmd.ExecuteReader();

                    if (dr.Read())
                    {
                        // Print results of SQL query in SQL Data Reader
                        Console.WriteLine($"File Path: \n {dr["file_path"]} \n");
                        Console.WriteLine($"Secret: \n {dr["secret_decrypted"]}");
                    }
                    dr.Close();
                }
            }
            catch (Exception ex)
            {
                //display error message
                Console.WriteLine("Exception: " + ex.Message);
            }
        }

        // Read in the content of a text file from local disk
        static string ReadFile(string path)
        {
            string content = "";
            try
            {
                content = System.IO.File.ReadAllText(path);
            }
            catch (Exception e)
            {
                Console.WriteLine("Error reading file: " + e.Message);
            }
            return content;
        }
    }
}