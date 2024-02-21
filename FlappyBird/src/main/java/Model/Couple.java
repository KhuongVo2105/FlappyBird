package Model;

public class Couple {
    private Pipe top, bottom;
    private int boardWidth, boardHeight; // width and height of the game board



    public Couple() {
    }

    public Couple(Pipe top, Pipe bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public Pipe getTop() {
        return top;
    }

    public void setTop(Pipe top) {
        this.top = top;
    }

    public Pipe getBottom() {
        return bottom;
    }

    public void setBottom(Pipe bottom) {
    }

    public void next() {
        this.getTop().setX(this.getTop().getX() - 1);
        this.getBottom().setX(this.getBottom().getX() - 1);
    }
}
