'''
Created on Jul 22, 2011

@author: camil
'''

class MetricException(Exception):
    def __init__(self, metric):
        self.Metric = metric
    
    def __str__(self):
        return 'Exception occured for metric ' + str(self.Metric)

class ExtractionFailed(MetricException):
    def __init__(self, metric, line):
        self.Metric = metric
        self.Line = line
        
    def __str__(self):
        return 'Metric ' + str(self.Metric) + ' could not extract from "' + self.Line + '".'
    
    
class ValueNotAvailable(MetricException):
    def __init__(self, metric):
        self.Metric = metric
    
    def __str__(self):
        return 'Metric ' + str(self.Metric) + ' has not found a value.'
    

class ValueNotFinalized(MetricException):
    def __init__(self, metric):
        self.Metric = metric
    
    def __str__(self):
        return 'Metric ' + str(self.Metric) + ' has not finalized its value.'
         

class FactorNotAvailable(MetricException):
    def __init__(self, metric, factormetric):
        self.Metric = metric
        self.FactorMetric = factormetric
        
    def __str__(self):
        return 'Metric ' + str(self.Metric) + ' cannot use factor ' + str(self.FactorMetric) + '.'
    

class AggregationFailed(MetricException):
    def __init__(self, metric, reason):
        self.Metric = metric
        
        # The reason is an exception too
        self.Reason = reason
        
    def __str__(self):
        return 'Metric ' + str(self.Metric) + ' could be aggregated. Reason: "' + str(self.Reason) + '".'