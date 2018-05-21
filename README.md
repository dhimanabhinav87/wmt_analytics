# wmt_analytics
This is a cooking.com recipe big data pipeline solution
The data is of the format :

recipe_id, recipe_name, description, ingredient, active, updated_date, created_date
1, pasta, Italian pasta, tomato sauce, true, 2018-01-09 10:00:57,  2018-01-10 13:00:57
1, pasta, null, cheese, true, 2018-01-09 10:10:57,  2018-01-10 13:00:57
2, lasagna, layered lasagna, cheese, true, 2018-01-09 10:00:57,  2018-01-10 13:00:57
2, lasagna, layered lasagna, blue cheese, false, 2018-01-09 10:00:57,  2018-01-10 13:00:57	…

Expected file size is 1TB per hour

Data Model : 
Cassandra Tables : Used by front end apps 
wmt_analytics.recipe_ingredients :https://github.com/dhimanabhinav87/wmt_analytics/blob/master/wmt_sbt/src/main/scala/com/cooking/recipeanalytics/cassandra/recipe_ingredients.ddl
wmt_analytics.recipes: https://github.com/dhimanabhinav87/wmt_analytics/blob/master/wmt_sbt/src/main/scala/com/cooking/recipeanalytics/cassandra/recipes.ddl

Hive Tables : 
wmt_analytics.recipe_hourly: https://github.com/dhimanabhinav87/wmt_analytics/blob/master/wmt_sbt/src/main/scala/com/cooking/recipeanalytics/hive/recipe_houry_ddl.hql

Hive Queries for Business Analytics: 
https://github.com/dhimanabhinav87/wmt_analytics/tree/master/wmt_sbt/src/main/scala/com/cooking/recipeanalytics/hive

Details are here in PPT: 
https://github.com/dhimanabhinav87/wmt_analytics/blob/master/wmt_cooking.pdf
