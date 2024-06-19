package dev.triumphteam.website.docs.markdown.highlight.language.languages

import dev.triumphteam.website.docs.markdown.highlight.HighlightType
import dev.triumphteam.website.docs.markdown.highlight.language.ANNOTATION_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.CHAR_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.CONSTANT_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.FUNCTION_REGEX
import dev.triumphteam.website.docs.markdown.highlight.language.GENERIC_FUNCTION_REGEX
import dev.triumphteam.website.docs.markdown.highlight.language.GENERIC_LAMBDA_FUNCTION_REGEX
import dev.triumphteam.website.docs.markdown.highlight.language.HighlightValidator
import dev.triumphteam.website.docs.markdown.highlight.language.LAMBDA_FUNCTION_REGEX
import dev.triumphteam.website.docs.markdown.highlight.language.LanguageDefinition
import dev.triumphteam.website.docs.markdown.highlight.language.MULTILINE_COMMENT_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.NUMBER_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.SLASH_COMMENT_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.STRING_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.StepValidator
import dev.triumphteam.website.docs.markdown.highlight.language.TYPE_COMPONENT
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.PunctuationHighlighter
import dev.triumphteam.website.docs.markdown.highlight.language.highlighter.RegexHighlighter

private val KOTLIN_KEYWORDS = """
    actual,abstract,annotation,as,as?,break,by,catch,class,companion,const,constructor,continue,
    coroutine,crossinline,data,delegate,dynamic,do,else,enum,expect,external,false,final,
    finally,for,fun,get,if,import,in,!in,infix,inline,interface,internal,is,!is,lazy,lateinit,
    native,null,object,open,operator,out,override,package,private,protected,public,reified,
    return,sealed,set,super,suspend,tailrec,this,throw,true,try,typealias,typeof,val,var,vararg,
    when,while,yield
""".trimIndent().split(",").map(String::trim).map { "\\b$it\\b".toRegex() }

public object KotlinLanguage : LanguageDefinition(
    components = listOf(
        SLASH_COMMENT_COMPONENT,
        MULTILINE_COMMENT_COMPONENT,
        STRING_COMPONENT,
        CHAR_COMPONENT,
        PunctuationHighlighter("{}()[].-=+-*$?<>:;&|".toList()),
        RegexHighlighter(type = HighlightType.KEYWORD, expressions = KOTLIN_KEYWORDS),
        RegexHighlighter(
            type = HighlightType.FUNCTION,
            expressions = listOf(
                FUNCTION_REGEX, // Normal calls
                LAMBDA_FUNCTION_REGEX, // Lambda calls
                GENERIC_FUNCTION_REGEX, // Generics calls
                GENERIC_LAMBDA_FUNCTION_REGEX, // Generics lambda calls
                // TODO: "(?<=\\w\\s)(\\w+)(?=\\s\\w)".toRegex(), // infix calls
            ),
        ),
        TYPE_COMPONENT,
        ANNOTATION_COMPONENT,
        RegexHighlighter(
            type = HighlightType.LABEL,
            expressions = listOf("@\\w+\\b".toRegex()), // Label,
        ),
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
        StepValidator.TypeAndFunction,
    ),
)
