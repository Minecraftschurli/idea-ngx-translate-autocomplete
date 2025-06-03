package fr.enzomallard.ngxtranslatetoolset.psi

import com.intellij.lang.javascript.patterns.JSPatterns.jsCallExpression
import com.intellij.lang.javascript.patterns.JSPatterns.jsReferenceExpression
import com.intellij.lang.javascript.psi.JSArgumentList
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PatternConditionPlus
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.PsiElementPattern.Capture
import com.intellij.patterns.StandardPatterns.string
import com.intellij.util.PairProcessor
import com.intellij.util.ProcessingContext
import org.angular2.lang.expr.psi.Angular2PipeExpression
import org.angular2.lang.expr.psi.Angular2PipeReferenceExpression
import org.angular2.lang.html.psi.Angular2HtmlTemplateBindings


object ElementPatterns {
    private val TS_CALL_EXP_INSTANT = psiElement(JSCallExpression::class.java)
        .withChild(jsReferenceExpression()
            .withName(string()
                .oneOf(
                    TranslationUtils.FRAMEWORKS
                        .map(TranslationFramework::functionName)
                )
            )
        )

    private val HTML_PIPE_EXP_TRANSLATE: Capture<Angular2PipeExpression> = psiElement(Angular2PipeExpression::class.java)
        .withChild(psiElement(Angular2PipeReferenceExpression::class.java)
            .withChild(psiElement()
                .withText(string()
                    .oneOf(
                        TranslationUtils.FRAMEWORKS
                            .map(TranslationFramework::pipeName)
                    )
                )
            )
        )

    private val HTML_DIRECTIVE_FUNCTION_CALL = jsCallExpression()
        .withFirstChild(jsReferenceExpression()
            .referencing(psiElement(JSVariable::class.java)
                .withSuperParent(6, psiElement(Angular2HtmlTemplateBindings::class.java)
                    .withTemplateName(string()
                        .oneOf(
                            TranslationUtils.FRAMEWORKS
                                .map(TranslationFramework::directiveName)
                                .filter { it != null }
                        )
                    )
                )
            )
        )

    val HTML_PIPE_TRANSLATION_PLATFORM_PATTERN: Capture<JSLiteralExpression> = psiElement(JSLiteralExpression::class.java)
        .inside(HTML_PIPE_EXP_TRANSLATE)

    val TS_TRANSLATION_PLATFORM_PATTERN: Capture<JSLiteralExpression> = psiElement(JSLiteralExpression::class.java)
        .withParent(psiElement(JSArgumentList::class.java)
            .withParent(TS_CALL_EXP_INSTANT)
        )

    val HTML_DIRECTIVE_TRANSLATION_PLATFORM_PATTERN: Capture<JSLiteralExpression> = psiElement(JSLiteralExpression::class.java)
        .withParent(psiElement(JSArgumentList::class.java)
            .withParent(HTML_DIRECTIVE_FUNCTION_CALL)
        )

    private fun Capture<Angular2HtmlTemplateBindings>.withTemplateName(pattern: ElementPattern<String>): Capture<Angular2HtmlTemplateBindings> {
        return this.with(object : PatternConditionPlus<Angular2HtmlTemplateBindings, String>("withTemplateName", pattern) {
            override fun processValues(
                t: Angular2HtmlTemplateBindings,
                context: ProcessingContext,
                processor: PairProcessor<in String, in ProcessingContext>
            ): Boolean = processor.process(t.templateName, context)
        })
    }
}
