package sky.terraberry;

@FunctionalInterface
public interface PixelGenerator
{
    public Pixel getPixel(int i,int j);
}
