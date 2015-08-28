package grails.plugin.markup.view

import grails.views.GenericGroovyTemplateResolver
import groovy.text.Template
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by graemerocher on 28/08/15.
 */
class MarkupViewCompilerSpec extends Specification {

    @Ignore
    void "Test MarkupViewCompiler compiles templates correctly"() {
        given: "A compiler instance"
        def view = new File(MarkupViewCompilerSpec.getResource("/views/bar.gxml").file)

        def config = new MarkupViewConfiguration(packageName: "test")
        def compiler = new MarkupViewCompiler(config, view.parentFile)

        def dir = File.createTempDir()
        dir.deleteOnExit()
        compiler.setTargetDirectory(dir)
        def resolver = new GenericGroovyTemplateResolver()
        resolver.classLoader = new URLClassLoader([dir.toURL()] as URL[])
        def engine = new MarkupViewTemplateEngine(config)
        engine.templateResolver = resolver


        when: "templates are compiled"

        compiler.compile(view)
        def template = engine.resolveTemplate("/bar.gxml")

        then: "The template can be loaded"
        template != null

        when:"The template is run"
        def writable = template.make(cars: [[make:"Audi", model:"A5"]])

        def writer = new StringWriter()
        writable.writeTo(writer)

        then:"The output is correct"
        writer.toString() == '''<?xml version='1.0'?>
<cars><car make='Audi' model='A5'/></cars>'''

    }
}