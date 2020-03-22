package com.ocelot.opendevices.api.util.icon;

import com.ocelot.opendevices.OpenDevices;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>A collection of in-built alphabet icons that can be used as an implementation of {@link IIcon}.</p>
 *
 * @author MrCrayfish
 */
public enum Alphabet implements IIcon
{
    EXCLAMATION_MARK('!'),
    QUOTATION_MARK('"'),
    NUMBER_SIGN('#'),
    DOLLAR_SIGN('$'),
    PERCENT_SIGN('%'),
    AMPERSAND('&'),
    APOSTROPHE('\''),
    LEFT_PARENTHESIS('('),
    RIGHT_PARENTHESIS(')'),
    ASTERISK('*'),
    PLUS_SIGN('+'),
    COMMA(','),
    HYPHEN_MINUS('-'),
    FULL_STOP('.'),
    SLASH('/'),
    ZERO('0'),
    ONE('1'),
    TWO('2'),
    THREE('3'),
    FOUR('4'),
    FIVE('5'),
    SIX('6'),
    SEVEN('7'),
    EIGHT('8'),
    NINE('9'),
    COLON(':'),
    SEMI_COLON(';'),
    LESS_THAN('<'),
    EQUALS('='),
    MORE_THAN('>'),
    QUESTION_MARK('?'),
    COMMERCIAL_AT('@'),
    UPPERCASE_A('A'),
    UPPERCASE_B('B'),
    UPPERCASE_C('C'),
    UPPERCASE_D('D'),
    UPPERCASE_E('E'),
    UPPERCASE_F('F'),
    UPPERCASE_G('G'),
    UPPERCASE_H('H'),
    UPPERCASE_I('I'),
    UPPERCASE_J('J'),
    UPPERCASE_K('K'),
    UPPERCASE_L('L'),
    UPPERCASE_M('M'),
    UPPERCASE_N('N'),
    UPPERCASE_O('O'),
    UPPERCASE_P('P'),
    UPPERCASE_Q('Q'),
    UPPERCASE_R('R'),
    UPPERCASE_S('S'),
    UPPERCASE_T('T'),
    UPPERCASE_U('U'),
    UPPERCASE_V('V'),
    UPPERCASE_W('W'),
    UPPERCASE_X('X'),
    UPPERCASE_Y('Y'),
    UPPERCASE_Z('Z'),
    LEFT_SQUARE_BRACKET('['),
    SLASH_REVERSE('\\'),
    RIGHT_SQUARE_BRACKET(']'),
    CARET('^'),
    UNDERSCORE('_'),
    GRAVE_ACCENT('`'),
    LEFT_CURLY_BRACKET('{'),
    VERTICAL_LINE('|'),
    RIGHT_CURLY_BRACKET('}'),
    TILDE('~'),
    UNKNOWN((char) 0xFFFD);

    private static final ResourceLocation ICON_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/alphabet.png");
    private static final Map<Character, Alphabet> NAME_LOOKUP = new HashMap<>();

    private static final int ICON_SIZE = 10;
    private static final int GRID_SIZE = 20;

    private char character;

    Alphabet(char character) {this.character = character;}

    @Override
    public ResourceLocation getIconLocation()
    {
        return ICON_LOCATION;
    }

    @Override
    public int getU()
    {
        return (this.ordinal() % GRID_SIZE) * ICON_SIZE;
    }

    @Override
    public int getV()
    {
        return (this.ordinal() / GRID_SIZE) * ICON_SIZE;
    }

    @Override
    public int getWidth()
    {
        return ICON_SIZE;
    }

    @Override
    public int getHeight()
    {
        return ICON_SIZE;
    }

    @Override
    public int getGridWidth()
    {
        return GRID_SIZE;
    }

    @Override
    public int getGridHeight()
    {
        return GRID_SIZE;
    }

    @Override
    public int getIndex()
    {
        return this.ordinal();
    }

    /**
     * @return The character representing this icon
     */
    public char getCharacter()
    {
        return character;
    }

    /**
     * Looks up the alphabet icon for the specified character.
     *
     * @param character The character to look for
     * @return The alphabet character or {@link #UNKNOWN} if there is no icon for that character
     */
    public static Alphabet getCharacter(char character)
    {
        return NAME_LOOKUP.getOrDefault(character, UNKNOWN);
    }

    /**
     * Looks up the alphabet icons for the specified char sequence.
     *
     * @param sequence The sequence of characters to look for
     * @return The alphabet characters array
     */
    public static Alphabet[] getCharSequence(CharSequence sequence)
    {
        Alphabet[] alphabet = new Alphabet[sequence.length()];
        for (int i = 0; i < sequence.length(); i++)
        {
            alphabet[i] = getCharacter(sequence.charAt(i));
        }
        return alphabet;
    }

    static
    {
        for (Alphabet alphabet : Alphabet.values())
        {
            NAME_LOOKUP.put(alphabet.getCharacter(), alphabet);
        }
    }
}
