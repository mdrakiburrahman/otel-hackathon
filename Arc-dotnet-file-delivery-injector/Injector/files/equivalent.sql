USE Controller;
OPEN SYMMETRIC KEY ControllerDbSymmetricKey DECRYPTION BY PASSWORD = 'HnUnfSCkkCtMeRZbN3AQvgIDyTVNBSVl';

SELECT file_path, secret_decrypted FROM (
    select *, convert(varchar(max), DECRYPTBYKEY(data)) as 'secret_decrypted' from controller.dbo.Files
) AS T
WHERE file_path = '/config/namespaces/arc-primary/scaledsets/gpm0mi01/containers/fluentbit/files/fluentbit-out-elasticsearch.conf'
ORDER BY created_time DESC