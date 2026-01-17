package dev.triumphteam.website.docs.markdown.highlight.language.languages

import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.language.ANNOTATION_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.CHAR_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.CONSTANT_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.FUNCTION_REGEX
import dev.triumphteam.website.docs.markdown.highlight.language.GENERIC_FUNCTION_REGEX
import dev.triumphteam.website.docs.markdown.highlight.language.HighlightValidator
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import dev.triumphteam.website.docs.markdown.highlight.language.MULTILINE_COMMENT_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.NUMBER_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.SLASH_COMMENT_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.STRING_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.StepValidator
import dev.triumphteam.website.docs.markdown.highlight.language.TYPE_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.PunctuationHighlighter
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.RegexHighlighter

private val Java_KEYWORDS = """
    abstract,assert,boolean,break,byte,case,catch,char,class,const,continue,
    default,do,double,else,enum,extends,final,finally,float,for,goto,if,implements,
    import,instanceof,int,interface,long,native,new,null,package,private,protected,
    public,return,short,static,strictfp,super,switch,synchronized,this,throw,throws,
    transient,try,void,volatile,while,var
""".trimIndent().split(",").map(String::trim).map { "\\b$it\\b".toRegex() }

public object JavaLanguage : LanguageDefinition(
    name = "java",
    components = listOf(
        SLASH_COMMENT_COMPONENT,
        MULTILINE_COMMENT_COMPONENT,
        STRING_COMPONENT,
        CHAR_COMPONENT,
        PunctuationHighlighter("!#$%&'()*+,-./:;<=>?@[]^_`{|}~".toList()),
        RegexHighlighter(type = HighlightType.KEYWORD, expressions = Java_KEYWORDS),
        RegexHighlighter(
            type = HighlightType.FUNCTION,
            expressions = listOf(
                FUNCTION_REGEX, // Normal calls
                GENERIC_FUNCTION_REGEX, // Generics calls
            ),
        ),
        TYPE_COMPONENT,
        ANNOTATION_COMPONENT,
        NUMBER_COMPONENT,
        CONSTANT_COMPONENT,
    ),
    globalValidator = listOf(
        // The order matters, string first is easier
        HighlightValidator.InString,
        HighlightValidator.InComment,
        HighlightValidator.InAnnotation,
    ),
    stepValidator = listOf(
        StepValidator.AnnotationAndFunction,
        StepValidator.KeywordAndFunction,
        StepValidator.TypeAndFunction,
    ),
)
