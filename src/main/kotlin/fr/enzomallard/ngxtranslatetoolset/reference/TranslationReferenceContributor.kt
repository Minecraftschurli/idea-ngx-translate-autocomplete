package fr.enzomallard.ngxtranslatetoolset.reference

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import fr.enzomallard.ngxtranslatetoolset.psi.ElementPatterns
import org.angular2.lang.expr.Angular2Language

class TranslationReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            ElementPatterns.HTML_PIPE_TRANSLATION_PLATFORM_PATTERN.withLanguage(Angular2Language),
            TranslationReferenceProviderPipe()
        )

        registrar.registerReferenceProvider(
            ElementPatterns.HTML_DIRECTIVE_TRANSLATION_PLATFORM_PATTERN.withLanguage(Angular2Language),
            TranslationReferenceProviderDirective()
        )

        registrar.registerReferenceProvider(
            ElementPatterns.TS_TRANSLATION_PLATFORM_PATTERN,
            TranslationReferenceProviderTS()
        )
    }
}
