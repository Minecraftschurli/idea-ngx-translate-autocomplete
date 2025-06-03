package fr.enzomallard.ngxtranslatetoolset.reference

import com.intellij.lang.javascript.hierarchy.JSHierarchyUtils
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import fr.enzomallard.ngxtranslatetoolset.psi.TranslationFramework
import fr.enzomallard.ngxtranslatetoolset.psi.TranslationUtils
import org.angular2.lang.html.psi.Angular2HtmlTemplateBindings

/**
 * Provide referencing for translation keys used in a function bound by a structural directive
 */
class TranslationReferenceProviderDirective : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val directive = JSHierarchyUtils
            .getTypeHierarchyTargetElement(element.parent.parent.firstChild)
            .let { PsiTreeUtil.getParentOfType(it, Angular2HtmlTemplateBindings::class.java) }

        if (directive == null) return arrayOf()

        if (TranslationUtils.FRAMEWORKS
            .map(TranslationFramework::directiveName)
            .filter { it != null }
            .none { funName -> directive.templateName == funName })
            return arrayOf()

        return arrayOf(TranslationReference(element as JSLiteralExpression, TextRange.allOf(element.text)))
    }
}
