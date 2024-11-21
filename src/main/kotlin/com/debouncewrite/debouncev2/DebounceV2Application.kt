package com.debouncewrite.debouncev2

import com.debouncewrite.debouncev2.common.util.StringUtils
import com.debouncewrite.debouncev2.infrastructure.config.constants.Properties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//@EnableR2dbcRepositories
@SpringBootApplication
class DebounceV2Application

fun main(args: Array<String>) {
	Properties
	runApplication<DebounceV2Application>(*args)
}
