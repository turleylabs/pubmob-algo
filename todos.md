# TODOS

- [x] Make sure we are green
- [ ] State Machine
  - [x] Isolate Took Profits
  - [x] Work out how many states
- [x] handle did not take profits state
- [x] create abstraction for moving averages
- [x] Pass averages to state, not moving averages
- [x] Line 105--return state from sell / buy 
- [X] Diagram current states

# Next

- [x] Introduce State Machine with took profits and everything else
- [x] Review state diagram
- [x] Make the state inner classes named (non-anonymous) 
    - [x] extract conditionals
    - [x] Wrapping methods from BaseAlgorithm
- [ ] Testing states
    - [x] Continue creating data/objects to get ReadyToBuyTest running
    - [x] Extract the shouldBuy predicate into an interface that we can then mock and inject into our test
    - [ ] Enumerate tests for ReadyToBuy state transitions (Bought Above 50, Bought Below 50, Do Not Buy)
    - [ ] Assign value of `priceBelow50` appropriate to test name
    - [ ] Clean up setup to make it obvious and make test narrative clear 
- [x] Create state for 50day MA flag

- [ ] Diagram new objective (add new states for new requirement)

## Optional  
- [ ] We_Hold state - is it "clean"? !not really selling / should liquidate
- [ ] Clean Did_not_take_profit states - RefactorMeAlgorithm.this
- [ ] Line 51 return this rather than named state

## State Diagram
https://docs.google.com/drawings/d/1ACPp40CmuMe63efOmIy8IDz2IfcC0pbR8bS5wLg-Ya0/edit?usp=sharing

