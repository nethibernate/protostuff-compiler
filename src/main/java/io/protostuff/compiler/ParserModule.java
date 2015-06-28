package io.protostuff.compiler;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import io.protostuff.compiler.parser.*;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.BailErrorStrategy;

import javax.inject.Named;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static io.protostuff.compiler.parser.DefaultDescriptorProtoProvider.DESCRIPTOR_PROTO;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class ParserModule extends AbstractModule {

    private final List<Path> protoIncludePathList;

    public ParserModule() {
        this.protoIncludePathList = Collections.emptyList();
    }

    public ParserModule(List<Path> protoIncludePathList) {
        this.protoIncludePathList = protoIncludePathList;
    }

    @Override
    protected void configure() {
        bind(Importer.class).to(ImporterImpl.class);
        bind(FileDescriptorLoader.class).to(FileDescriptorLoaderImpl.class);
        bind(ANTLRErrorListener.class).to(ParseErrorLogger.class);
        bind(ANTLRErrorStrategy.class).to(BailErrorStrategy.class);
        bind(ExtensionRegistry.class).to(LocalExtensionRegistry.class);
        bind(ProtoContext.class)
                .annotatedWith(Names.named(DESCRIPTOR_PROTO))
                .toProvider(DefaultDescriptorProtoProvider.class);
        Multibinder<ProtoPostProcessor> uriBinder = Multibinder.newSetBinder(binder(), ProtoPostProcessor.class);
        uriBinder.addBinding().to(ExtensionsPostProcessor.class);
        uriBinder.addBinding().to(OptionsPostProcessor.class);
    }



    @Provides
    ProtoParserListener listener() {
        return null;
    }

    @Provides
    FileReader fileReader() {
        ClasspathFileReader classpathFileReader = new ClasspathFileReader();
        LocalFileReader localFileReader = new LocalFileReader(protoIncludePathList);
        return new CompositeFileReader(localFileReader, classpathFileReader);
    }

}
