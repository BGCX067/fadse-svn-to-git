import re

# Class to represent an indicator that will be collected from the statistical data.
# Title = the name that will be written in the output CSV file as column title for the indicator
# Value = the useful value extracted from the statistical data, AFTER it has been modified by Operation
# Source = the name of the indicator, as it is found in the statistical data
# Regex = the regex pattern used to extract the source value from a given line
# RegexGroup = the group in the regex pattern containing the source value
# Operation = operation to be applied to the source indicator, along with X, to obtain the meaningful value of the indicator
# X = the factor used by Operation when calculating the meaningful value

class Indicator:
    def __init__(self, title, source, r, rgroup, op='mul', x=1.0):
        self.Title = title
        self.Value = 0.0
        self.Source = source
        self.Operation = op
        self.Regex = re.compile(r)
        self.RegexGroup = rgroup
        self.X = x

# Test if the given line from the statistical data holds the source value for this indicator
    def extract(self, line):
        if line.find(self.Source) != -1:
            try:
                SourceValue = float(self.Regex.search(line).group(self.RegexGroup))
            except AttributeError:
                # The simulator tried to report this value, but it's not a number
                # (could be "<error: divide by zero>")
                #print 'Jython error: Regex group', self.RegexGroup, 'not found in', line
                return False
            if self.Operation == 'mul':
                self.Value = SourceValue * self.X
            elif self.Operation == 'div':
                self.Value = SourceValue / self.X
            elif self.Operation == 'inv':
                self.Value = float(1) / SourceValue
            return True
        else:
            return False



class AggregatingIndicator:
    def __init__(self, title, indicator_list, aggregation_callback, factor, initial_value):
        self.Title = title
        self.InitialValue = initial_value
        self.Value = self.InitialValue
        self.Indicators = indicator_list
        self.AggregateCallback = aggregation_callback
        
        #print 'Aggregator ' + self.Title + ' initialized'
        
        # the Factor may be a numeric value or another Indicator object
        self.InitialFactor = factor
        self.Factor = factor
        
    def reset(self):
	#print 'Aggregator ' + self.Title + ' reset'
	self.Factor = self.InitialFactor
        self.Value = self.InitialValue
        
    def extract(self, line):
	status = False
        for indicator in self.Indicators:
            if indicator.extract(line):
		#print '\t Aggregator ' + self.Title + ', subIndicator: ' + indicator.Title + ' = ' + str(indicator.Value)
                try:
                    # if the Factor is numeric, use it
                    self.Value = self.AggregateCallback(self.Value, indicator.Value * self.Factor)
                except TypeError:
                    # if the Factor not numeric (it's an Indicator), don't use it
                    self.Value = self.AggregateCallback(self.Value, indicator.Value)
                status = True
        try:
            # if the Factor is an Indicator, test this line for it
            # in case the extraction test returns True, self.Factor will
            # become a numeric value, and it will be usable in the "for" loop above
            # also, multiply our current Value with the Factor
            if self.Factor.extract(line):
                self.Factor = self.Factor.Value
                #print 'Jython: Factor found: ' +self.Factor.Title
                self.Value = self.Value * self.Factor
                status = True
        except AttributeError:
            pass
        return status

def sum_aggregation(total, next):
    return total + next

def multiplicative_aggregation(total, next):
    return total * next