'''
Created on Jul 21, 2011

@author: camil
'''
import MetricDefinitions
from Metric import *
from AggregationOperators import *

CPIMetric = None
NumCoresMetric = None
MulticoreEnergy = None
Metrics = {}

def createMulticoreEnergy(nCores):
    global Metrics
    PowersForAllCores = []
    for core in range(int(nCores)):
        CoreName = 'Core_' + str(core) + '_' 
        PowersForAllCores.append(
            AggregatingMetric(
                CoreName + 'power',
                MetricDefinitions.getPowersPerCore(CoreName),
                Sum
            )
        )
    
    Metrics['Multicore_energy'].Metrics.extend(PowersForAllCores)
    
def initialize():
    print 'Jython: Initializing Extractor...'
    global CPIMetric
    global NumCoresMetric
    global Metrics
    
    CPIMetric = MetricDefinitions.getCPIMetric()
    NumCoresMetric = MetricDefinitions.getNumCoresMetric(createMulticoreEnergy)
    
    ItlbPowerMetric = MetricDefinitions.getItlbPowerMetric()
    DtlbPowerMetric = MetricDefinitions.getDtlbPowerMetric()
     

    Metrics['NumCores'] = NumCoresMetric
    Metrics['CPI'] = CPIMetric
    MulticoreEnergy = AggregatingMetric('Multicore_energy', 
                                        [ItlbPowerMetric, DtlbPowerMetric], 
                                        Sum, 
                                        'mul', 
                                        Metrics['CPI']
                                        )
    
    Metrics['Multicore_energy'] = MulticoreEnergy
    
    print 'Jython: Extractor initialized.'
        
def processLine(line):
    global Metrics
    for metric in Metrics.values():
        metric.processLine(line)
        
def finalize():
    global Metrics
    map(Metric.finalize, Metrics.values())
    
def getMetricValue(metricID):
    return Metrics[metricID].getValue()
    