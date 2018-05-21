CREATE TABLE `wmt_analytics.recipe_hourly`(
  `recipe_id` int,
  `recipe_name` varchar(128),
  `description` string,
  `ingredient` varchar(128),
  `active` boolean,
  `update_date` timestamp,
  `created_date` timestamp)
PARTITIONED BY (
  `source_file_date` string,
  `hour` string)
ROW FORMAT SERDE
  'org.apache.hadoop.hive.ql.io.orc.OrcSerde'
STORED AS INPUTFORMAT
  'org.apache.hadoop.hive.ql.io.orc.OrcInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat'