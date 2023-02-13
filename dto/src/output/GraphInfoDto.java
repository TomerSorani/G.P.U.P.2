package output;

public class GraphInfoDto {
    final private Integer targetsAmount, rootsAmount, middlesAmount, leavesAmount, independentsAmount;

    public GraphInfoDto(Integer targetsAmountInput, Integer rootsAmountInput, Integer middlesAmountInput, Integer leavesAmountInput, Integer independentsAmountInput) {
        targetsAmount = targetsAmountInput;
        rootsAmount = rootsAmountInput;
        middlesAmount = middlesAmountInput;
        leavesAmount = leavesAmountInput;
        independentsAmount = independentsAmountInput;
    }

    final public Integer getTargetsAmount() {
        return targetsAmount;
    }

    final public Integer getRootsAmount() {
        return rootsAmount;
    }

    final public Integer getMiddlesAmount() {
        return middlesAmount;
    }

    final public Integer getLeavesAmount() {
        return leavesAmount;
    }

    final public Integer getIndependentAmount() {
        return independentsAmount;
    }
}
