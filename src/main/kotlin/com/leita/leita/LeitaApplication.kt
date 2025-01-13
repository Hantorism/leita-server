package com.leita.leita

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
class LeitaApplication

fun main(args: Array<String>) {
	runApplication<LeitaApplication>(*args)
}
