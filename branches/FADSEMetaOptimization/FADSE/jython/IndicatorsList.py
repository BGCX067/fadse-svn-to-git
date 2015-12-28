from Indicator import Indicator, AggregatingIndicator, sum_aggregation
from AggregatingIndicatorsList import Core_0_TotalPower, SingleCore_TotalPower


Cycles = Indicator('Cycles', 'sim_cycle', '(?<=sim_cycle)( +)([0-9\.]+)(?= #)', 2)

IndicatorsList = [
    Indicator('CPI', 'THROUGHPUT IPC', '(?<=IPC:)( +)([0-9\.]+)', 2, 'inv'),
    # AggregatingIndicator('Core_0_energy', Core_0_TotalPower, sum_aggregation, Cycles, 0),
    # AggregatingIndicator('Core_0_total_power', Core_0_TotalPower, sum_aggregation, 1, 0),
    AggregatingIndicator('singlecore_energy', SingleCore_TotalPower, sum_aggregation, Cycles, 0),
    AggregatingIndicator('singlecore_total_power', SingleCore_TotalPower, sum_aggregation, 1, 0)
]

# Return a list with the Title fields of all the Indicator objects in the IndicatorList
def get_indicator_titles():
    titles = []
    for i in IndicatorsList:
	titles.append(i.Title)
    return titles