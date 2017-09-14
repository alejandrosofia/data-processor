drop function if exists v5_dashboard.ballot_measure_contest_summary(integer);

create function v5_dashboard.ballot_measure_contest_summary(rid int)
returns void
as $$
  declare
    bmc_errors int;
    bmc_count int;
    bmc_completion int;

begin

select count(*) into bmc_errors
from xml_tree_validations
  where results_id = rid
  and (
    scope like 'ballot-measure-contest%'
    or
    path ~ 'VipObject.0.BallotMeasureContest.*'
  );

select count(distinct countable_path(path)) into bmc_count
from xml_tree_values
  where results_id = rid
  and path ~ 'VipObject.0.BallotMeasureContest.*';

raise notice 'bmc_errors = %', bmc_errors;
raise notice 'bmc_count = %', bmc_count;

update v5_statistics
  set
    ballot_measure_contest_errors = bmc_errors,
    ballot_measure_contest_count = bmc_count
  where results_id = rid;

end;

$$ language plpgsql;
