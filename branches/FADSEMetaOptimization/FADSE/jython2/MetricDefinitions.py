'''
Created on Jul 21, 2011

@author: camil
'''
from Metric import *

def getPowersPerCore(CoreName):
    CorePowerMetrics = [
        Metric(CoreName + 'alu_power', CoreName + 'avg_alu_power_cc3', '(?<=' + CoreName + 'avg_alu_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'dcache2_power', CoreName + 'avg_dcache2_power_cc3', '(?<=' + CoreName + 'avg_dcache2_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'dcache_power', CoreName + 'avg_dcache_power_cc3', '(?<=' + CoreName + 'avg_dcache_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'lsq_power', CoreName + 'avg_lsq_power_cc3', '(?<=' + CoreName + 'avg_lsq_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'bpred_power', CoreName + 'avg_bpred_power_cc3', '(?<=' + CoreName + 'avg_bpred_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'regfile_power', CoreName + 'avg_regfile_power_cc3', '(?<=' + CoreName + 'avg_regfile_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'falu_power', CoreName + 'avg_falu_power_cc3', '(?<=' + CoreName + 'avg_falu_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'rename_power', CoreName + 'avg_rename_power_cc3', '(?<=' + CoreName + 'avg_rename_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        # Metric(CoreName + 'lvpt_power', CoreName + 'avg_lvpt_power_cc3', '(?<=' + CoreName + 'avg_lvpt_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        # Metric(CoreName + 'regfile_power', CoreName + 'avg_regfile_power_cc3', '(?<=' + CoreName + 'avg_regfile_power_cc3)( +)([0-9\.]+)(?= #)', 2, 'div', 2),
        # Metric('Itlb_power', 'Itlb_power', '(?<=Itlb_power \(W\):)( +)([0-9\.]+)(?= \()', 2),
        Metric(CoreName + 'window_power', CoreName + 'avg_window_power_cc3', '(?<=' + CoreName + 'avg_window_power_cc3)( +)([0-9\.]+)(?= #)', 2),
        Metric(CoreName + 'icache_power', CoreName + 'avg_icache_power_cc3', '(?<=' + CoreName + 'avg_icache_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    ]
    return CorePowerMetrics

def getCPIMetric():
    return Metric('Cycles', 'sim_cycle', '(?<=sim_cycle)( +)([0-9\.]+)(?= #)', 2)

def getNumCoresMetric(onvaluefound):
    return TriggerMetric('NumCores', '-num_cores', '(?<=num_cores)( +)([0-9\.]+)(?= #)', 2, onvaluefound)

def getItlbPowerMetric():
    return Metric('Itlb_power', 'Itlb_power', '(?<=Itlb_power \(W\):)( +)([0-9\.]+)(?= \()', 2)

def getDtlbPowerMetric():
    return Metric('Dtlb_power', 'Dtlb_power', '(?<=Dtlb_power \(W\):)( +)([0-9\.]+)(?= \()', 2)
