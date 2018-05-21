select recipe_name
,hour
,count(*)  as UpdatedNumberOfTimes
from wmt_analytics.recipe_hourly group by recipe_name,hour;