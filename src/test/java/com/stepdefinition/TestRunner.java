package com.stepdefinition;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features", glue = {"com.stepdefinition"},
monochrome=true, plugin = {"pretty", "html:target/HtmlReports"})
public class TestRunner {
	
	

}
