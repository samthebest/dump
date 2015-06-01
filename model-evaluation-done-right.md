
It's called a "measure" not a "metric"

Start from the Use Case and work your way in.

Need example for marketting literature - classification.

Need example for clustering - think about ommissions and additions. Again this relates to marketting.

Clustering and classification are intrisically linked via a couple of mappings.

If you do not have any test data, you cannot evaluate your model, so just write a model that outputs random numbers.

Do not use AUC, Gini, F1-score, MCC (or the various "information theoretic" measures). This corresponds to the probability that your classfier is good, not the probability that your classification given a classifier is good - you have yet to choose the classifier. Use the ROC curve to understand your model.  Do not try to map a curve to a single number, it might be easier to compare a single number, but if our job constitutes comparing two real valued numbers to see which one is bigger then we are certainly rather overpaid!  We should be looking at the entire graph, all the data, do not throw away information.

Use your domain to define a measure of performance based on profitability and probability.

E.g. in advertising / marketing, precision, or lift maps directly to savings over random selection.

Lift is very nice because one can fix the business action, then yield a cost benifit.  Sometimes things are more complicated, sometimes you have a choice of many business actions, possibly infinitely many, you also have to think about utility, long term and short term strategy.  Ideally you have a nice well defined problem given to you by a superior or business analyst, but you always want to be able to challange that if the models suggest another strategy is optimial.

Try to avoid averaging up over multiple models.  E.g. you are trying to predict 10 things (say recommendation site), you use the same underlying library / model to predict all 10, you compute precision for all 10, do not average up.  Similarly if you have some time series problem - you are trying to predict when person X will quit your game, your model performance may change from time to time, do not average up the numbers.

AGAIN: Do not try to map a curve to a single number, it might be easier to compare a single number, but if our job constitutes comparing two real valued numbers to see which one is bigger then we are certainly rather overpaid!  We should be looking at the entire graph, all the data, do not throw away information.

Use 3D plots to understand model performance over time (e.g. ROC over time).




