print 'Analyzer initialized (Jython)'

import sys

# Objectives are extracted using a list of Indicator objects (Objective == Indicator in this problem)

DefinedObjectives = ''

def init(cwd):
    global DefinedObjectives
    global isInfeasible
    sys.path = ['__classpath__']
    #sys.path.append(cwd)
    sys.path.append(cwd + '/jython')
    sys.path.append(cwd + '/jython/lib')
    import IndicatorsList
    DefinedObjectives = IndicatorsList.IndicatorsList



def analyze(line, linenumber):
    global isInfeasible
    isInfeasible = False
    for Objective in DefinedObjectives:
        try:
            if Objective.extract(line):
                pass
                #print 'Jython: Objective found:', Objective.Title, Objective.Value
        except ZeroDivisionError:
            isInfeasible = True
    return



ExtractedObjectiveName = ''
ExtractedObjectiveValue = 0.0  
i = 0
def next_extracted_objective():
    # These two names must be declared "global", to be able to write to them
    global ExtractedObjectiveName
    global ExtractedObjectiveValue
    global i
    try:
        ExtractedObjectiveName = DefinedObjectives[i].Title
        ExtractedObjectiveValue = DefinedObjectives[i].Value
        i += 1
        return True
    except IndexError:
        return False
    
    
    


def reset_analyzer():
    for objective in DefinedObjectives:
        try:
            objective.reset()
        except:
            continue