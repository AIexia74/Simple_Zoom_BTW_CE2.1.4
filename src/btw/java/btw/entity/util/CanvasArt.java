// FCMOD

package btw.entity.util;

public enum CanvasArt
{
    Icarus( "Icarus", 64, 64, 0, 0),
    Dagon( "Dagon", 64, 64, 64, 0 ),
    Pentacle( "Pentacle", 64, 64, 128, 0 ),
    Dragon( "Dragon", 64, 64, 192, 0 ),
    TreeOfLife( "TreeOfLife", 64, 96, 0, 64 ),
    Magician( "Magician", 48, 96, 64, 64 ),
    HangedMan( "HangedMan", 48, 96, 112, 64 ),
    Death( "Death", 48, 96, 160, 64 ),
    Fool( "Fool", 48, 96, 208, 64 ),
    IsleOfDead( "IsleOfDead", 96, 48, 0, 160 );

    /** Holds the maximum length of paintings art title. */
    public static final int MAX_ART_TITLE_LENGTH = "SuperLongTestString".length();

    /** Painting Title. */
    public final String title;
    public final int sizeX;
    public final int sizeY;
    public final int offsetX;
    public final int offsetY;

    private CanvasArt(String sTitle, int iSizeX, int iSizeY, int iOffsetX, int iOffsetY )
    {
        title = sTitle;
        sizeX = iSizeX;
        sizeY = iSizeY;
        offsetX = iOffsetX;
        offsetY = iOffsetY;
    }
}
