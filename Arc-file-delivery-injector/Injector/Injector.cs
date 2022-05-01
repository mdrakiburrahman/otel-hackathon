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
            string connString = @"Server = 172.31.147.204; Database = controller; User ID = controldb-rw-user; Password = ZiehzGZM0GuxoRptQO8PKTZ3kz0B6En4;";
            string targetFile = @"/config/namespaces/arc/scaledsets/sql-gp-1/containers/fluentbit/files/fluentbit-out-elasticsearch.conf";
            string sourceFile = @"/workspaces/otel-hackathon/Arc-file-delivery-injector/Injector/files/elasticsearch_modified.conf";

            // Read in text file from drive
            String sourceText = ReadFile(sourceFile);
            Console.WriteLine(sourceText);

            try
            {
                using (SqlConnection conn = new SqlConnection(connString))
                {
                    // Open connection
                    conn.Open();

                    // Open Symmetric Key for this session
                    string query = @"OPEN SYMMETRIC KEY ControllerDbSymmetricKey DECRYPTION BY PASSWORD = 'c42kE28hkUZa-zt3SVPFVNjrTaIY6BGa';";
                    SqlCommand cmd = new SqlCommand(query, conn);
                    cmd.ExecuteNonQuery();

                    // Target File
                    query = $@"SELECT file_path, secret_decrypted FROM (
                        	    select *, convert(varchar(max), DECRYPTBYKEY(data)) as 'secret_decrypted' from controller.dbo.Files
	                        ) AS T
                            WHERE file_path = '{targetFile}'
                            ORDER BY created_time DESC";
                    cmd = new SqlCommand(query, conn);

                    // Execute the SQLCommand
                    SqlDataReader dr = cmd.ExecuteReader();

                    if (dr.Read())
                    {
                        // Print results of SQL query in SQL Data Reader
                        Console.WriteLine($"File Path: \n {dr["file_path"]} \n");
                        Console.WriteLine($"Secret: \n {dr["secret_decrypted"]}");
                    }
                    dr.Close();

                    // Test encryption and decryption
                    query = $@"DECLARE @sample_data varchar(max)
                            SET @sample_data = N'{sourceText}'

                            SELECT CONVERT(varchar(MAX), DecryptByKey(EncryptByKey(Key_GUID('ControllerDbSymmetricKey'), @sample_data))) AS data";

                    cmd = new SqlCommand(query, conn);

                    // Execute the SQLCommand
                    dr = cmd.ExecuteReader();

                    if (dr.Read())
                    {
                        // Print results of SQL query in SQL Data Reader
                        Console.WriteLine($"Data: \n{dr["data"]}");
                    }

                    // INSERT into SQL Server


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