package guard;

public class Check
{
    private Check(){}

    public static final Guard Against= new Guard();
    public static final Requirement That= new Requirement();

    public static class Guard {
        public void nullValue(Object input, String name) {
            if (input == null) throw new IllegalStateException(name + " is null");
        }

        public void nullValue(Object input) {
            if (input == null) throw new IllegalStateException("Value is null");
        }

        public void isBlank(String value, String name) {
            if (value.isBlank()) throw new IllegalStateException(name + " is blank");
        }
    }

    public static class Requirement {
        public void isLargerThan(int value, int min, String name){
            if (value <= min){
                throw new IllegalStateException(name + " must be larger than " + min);
            }
        }

        public void isLessThan(int value, int max, String name){
            if (value >= max){
                throw new IllegalStateException(name + " must be less than " + max);
            }
        }

        public void isLessThanOrEqual(int value, int max, String name){
            if (value > max){
                throw new IllegalStateException(name + " must be less than or equal " + max);
            }
        }

        public void isPositive(int value, String name) {
            if (value <= 0){
                throw new IllegalStateException(name + " must be positive");
            }
        }
    }
}
