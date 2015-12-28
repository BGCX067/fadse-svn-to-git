from Indicator import Indicator

Core_0_TotalPower = [
    Indicator('Core_0_ialu_power', 'Core_0_avg_ialu_power_cc3', '(?<=Core_0_avg_ialu_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Core_0_dcache2_power', 'Core_0_avg_dcache2_power_cc3', '(?<=Core_0_avg_dcache2_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Core_0_dcache_power', 'Core_0_avg_dcache_power_cc3', '(?<=Core_0_avg_dcache_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Dtlb_power', 'Dtlb_power', '(?<=Dtlb_power \(W\):)( +)([0-9\.]+)(?= \()', 2),
    Indicator('Core_0_lsq_power', 'Core_0_avg_lsq_power_cc3', '(?<=Core_0_avg_lsq_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Core_0_bpred_power', 'Core_0_avg_bpred_power_cc3', '(?<=Core_0_avg_bpred_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Core_0_regfile_power', 'Core_0_avg_regfile_power_cc3', '(?<=Core_0_avg_regfile_power_cc3)( +)([0-9\.]+)(?= #)', 2, 'div', 2),
    Indicator('Core_0_falu_power', 'Core_0_avg_falu_power_cc3', '(?<=Core_0_avg_falu_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Core_0_rename_power', 'Core_0_avg_rename_power_cc3', '(?<=Core_0_avg_rename_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    # Indicator('Core_0_lvpt_power', 'Core_0_avg_lvpt_power_cc3', '(?<=Core_0_avg_lvpt_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Core_0_regfile_power', 'Core_0_avg_regfile_power_cc3', '(?<=Core_0_avg_regfile_power_cc3)( +)([0-9\.]+)(?= #)', 2, 'div', 2),
    Indicator('Itlb_power', 'Itlb_power', '(?<=Itlb_power \(W\):)( +)([0-9\.]+)(?= \()', 2),
    Indicator('Core_0_window_power', 'Core_0_avg_window_power_cc3', '(?<=Core_0_avg_window_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Core_0_icache_power', 'Core_0_avg_icache_power_cc3', '(?<=Core_0_avg_icache_power_cc3)( +)([0-9\.]+)(?= #)', 2),
]

SingleCore_TotalPower = [
    Indicator('ialu_power', 'avg_ialu_power_cc3', '(?<=avg_ialu_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('dcache2_power', 'avg_dcache2_power_cc3', '(?<=avg_dcache2_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('dcache_power', 'avg_dcache_power_cc3', '(?<=avg_dcache_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Dtlb_power', 'Dtlb_power', '(?<=Dtlb_power \(W\):)( +)([0-9\.]+)(?= \()', 2),
    Indicator('lsq_power', 'avg_lsq_power_cc3', '(?<=avg_lsq_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('bpred_power', 'avg_bpred_power_cc3', '(?<=avg_bpred_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('regfile_power', 'avg_regfile_power_cc3', '(?<=avg_regfile_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('falu_power', 'avg_falu_power_cc3', '(?<=avg_falu_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('rename_power', 'avg_rename_power_cc3', '(?<=avg_rename_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('lvpt_power', 'avg_lvpt_power_cc3', '(?<=avg_lvpt_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('Itlb_power', 'Itlb_power', '(?<=Itlb_power \(W\):)( +)([0-9\.]+)(?= \()', 2),
    Indicator('window_power', 'avg_window_power_cc3', '(?<=avg_window_power_cc3)( +)([0-9\.]+)(?= #)', 2),
    Indicator('icache_power', 'avg_icache_power_cc3', '(?<=avg_icache_power_cc3)( +)([0-9\.]+)(?= #)', 2),
]