package com.leita.leita

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import java.util.*

@SpringBootApplication
@EnableFeignClients
class LeitaApplication {
	@PostConstruct
	fun init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
	}
}

fun main(args: Array<String>) {
	runApplication<LeitaApplication>(*args)
}
