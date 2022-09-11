package at.aleb.githubstargazers.di

import javax.inject.Qualifier

class QualifyDispatcher {

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class Main

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class IO

    @Retention(AnnotationRetention.BINARY)
    @Qualifier
    annotation class Default
}