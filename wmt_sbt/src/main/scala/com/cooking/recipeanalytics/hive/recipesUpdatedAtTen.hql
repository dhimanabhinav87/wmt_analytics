select recipe_name
,count(*) as NumberOfTimesUpdated
from wmt_analytics.recipe_hourly
where hour=10
and to_date(update_date)>= add_months(current_date,-12)
group by recipe_name;