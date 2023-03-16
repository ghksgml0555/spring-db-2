package hello.springtx.order;

//Exception상속 >> 체크예외
//비즈니스예외라서 롤백하고싶진 않다
public class NotEnoughMoneyException extends Exception{

    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
