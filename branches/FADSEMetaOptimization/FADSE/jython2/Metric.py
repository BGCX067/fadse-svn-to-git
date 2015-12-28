import re

import Exceptions
# Class to represent a metric that will be collected from the statistical data.

class Metric(object):
    def __init__(self, title, source, r, rgroup):
        # Human-readable identifier for the metric
        self.Title = title
        
        # The useful value extracted from the statistical data, after it has been modified by Operation
        self.Value = 0.0
        
        # The name of the metric as it is found in the statistical data
        self.Source = source
        
        # The regex pattern used to extract the source value from a given line
        try:
            self.Regex = re.compile(r)
        except TypeError:
            # The provided regex pattern was None, meaning that this metric won't get its value from a line 
            self.Regex = None
            pass
        
        # The index of the group in the regex pattern containing the value
        self.RegexGroup = rgroup
        
        self.ValueFound = False
        self.ValueFinalized = False

    def processLine(self, line):
        if self.ValueFound == False:
            try:
                self.extract(line)
            except Exceptions.ExtractionFailed:
                raise

    def extract(self, line):
        ValueOnLine = 0
        try:
            ValueOnLine = line.find(self.Source)
        except TypeError:
            return self.ValueFound
        
        if ValueOnLine != -1:
            try:
                self.setValue(float(self.Regex.search(line).group(self.RegexGroup)))
            except AttributeError:
                raise Exceptions.ExtractionFailed(self, line)
                self.ValueFound = False
    
        return self.ValueFound
    
    def setValue(self, value):
        self.Value = value
        self.ValueFound = True
        
    def getValue(self):
        if self.ValueFound == False:
            raise Exceptions.ValueNotAvailable(self)
        elif self.ValueFinalized == False:
            raise Exceptions.ValueNotFinalized(self)
        else:
            return self.Value
    
    def finalize(self):
        if self.ValueFound == True:
            self.ValueFinalized = True
        
    def __str__(self):
        return '<' + self.Title + '> = ' + str(self.Value)
            
    def reset(self):
        self.Value = 0.0
        self.ValueFound = False


class AdjustedMetric(Metric):
    def __init__(self, title, source, r, rgroup, op='mul', factor=1.0):
        Metric.__init__(self, title, source, r, rgroup)
        
        # The operation to be applied to the source metric, along with X, to obtain the meaningful value of the metric
        self.Operation = op
        
        # The other operand (besides self.Value), used by Operation when calculating the meaningful value
        self.Factor = factor
        
    def applyOperation(self):
        if self.Operation == 'mul':
            self.Value = self.getValue() * self.Factor
        elif self.Operation == 'div':
            self.Value = self.getValue() / self.Factor
        elif self.Operation == 'inv':
            self.Value = self.Factor / self.getValue()
    
    def processFactor(self, line):
        try:
            self.Factor.processLine(line)
        except:
            pass
        
    def processLine(self, line):
        Metric.processLine(self, line)
        self.processFactor(line)
    
    def prepareFactor(self):
        try:
            self.Factor.finalize()
        except AttributeError:
            # This factor is numeric
            return
        try:
            self.Factor = self.Factor.getValue()
        except Exception:
            raise Exceptions.FactorNotAvailable(self, self.Factor)
    
    def finalize(self):
        self.prepareFactor()
        Metric.finalize(self)
        self.applyOperation()
        

class TriggerMetric(AdjustedMetric):
    def __init__(self, title, source, r, rgroup, onvaluefound, op='mul', factor=1.0):
        AdjustedMetric.__init__(self, title, source, r, rgroup, op, factor)
        
        self.OnValueFound = onvaluefound
        
    def setValue(self, value):
        AdjustedMetric.setValue(self, value)
        self.OnValueFound(value)

class AggregatingMetric(AdjustedMetric):
    def __init__(self, title, metric_list, agg_op, op='mul', factor=1.0):
        # An aggregating metric cannot be extracted from one line, like the Metric or AdjustedMetric,
        # so its superclass is initialized with None values
        AdjustedMetric.__init__(self, title, None, None, 0, op, factor)
        
        # The list of metrics to be aggregated
        self.Metrics = metric_list
        
        # The aggregation operator to be applied to the metrics
        self.AggregationOperator = agg_op
        
    def reset(self):
        pass
        
    def processLine(self, line):
        # Test for the Factor too
        AdjustedMetric.processLine(self, line)
        
        # Every metric gets a chance to process the given line
        for metric in self.Metrics:
            metric.processLine(line)

    def aggregateMetrics(self):
        try:
            self.Value = self.AggregationOperator(self.Metrics)
        except Exception as E:
            raise Exceptions.AggregationFailed(self, E)
        else:
            self.ValueFound = True
        
    
    def finalize(self):
        # Finalize and aggregate all metrics
        for metric in self.Metrics:
            metric.finalize()
        self.aggregateMetrics()
        
        AdjustedMetric.finalize(self)
        
        
        