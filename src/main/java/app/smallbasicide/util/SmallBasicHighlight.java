package app.smallbasicide.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class SmallBasicHighlight extends Highlight {

    private static final String PAREN_PATTERN     = "\\(|\\)";
    private static final String BRACE_PATTERN     = "\\{|\\}";
    private static final String BRACKET_PATTERN   = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN    = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN   = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private Pattern pattern;

    /**
     * Initialize with CodeArea.
     * @param codeArea
     */
    public SmallBasicHighlight(final CodeArea codeArea) {
        super(codeArea);
    }

    @Override
    protected StyleSpans<Collection<String>> computeHighlighting(final String text) {
        if (pattern == null) {
            makePattern();
        }
        final Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            final String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Make keyword pattern.
     */
    private void makePattern() {
        List<String> keywordsList = Arrays.asList(
                "For", "Let", "If", "EndIf", "While",
                "EndFor", "Sub", "EndSub", "True",
                "False", "Else", "To", "Step", "EndWhile",
                "Then", "Print", "Do"
        );
        try (final Stream<String> lines = keywordsList.stream()) {
            final String keywords = lines.collect(Collectors.joining("|"));
            final String pattern  = "\\b(" + keywords + ")\\b";
            this.pattern = Pattern.compile(
                    "(?<KEYWORD>" + pattern + ")"
                            + "|(?<PAREN>" + PAREN_PATTERN + ")"
                            + "|(?<BRACE>" + BRACE_PATTERN + ")"
                            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                            + "|(?<STRING>" + STRING_PATTERN + ")"
                            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            );
        }
    }
}
