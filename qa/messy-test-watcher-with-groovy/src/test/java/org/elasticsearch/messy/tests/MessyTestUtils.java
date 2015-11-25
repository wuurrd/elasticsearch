/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.messy.tests;

import org.apache.lucene.util.LuceneTestCase;
import org.elasticsearch.common.SuppressForbidden;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.script.ScriptContextRegistry;
import org.elasticsearch.script.ScriptEngineService;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.script.groovy.GroovyScriptEngineService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;
import org.elasticsearch.watcher.support.init.proxy.ScriptServiceProxy;
import org.elasticsearch.watcher.support.text.xmustache.XMustacheScriptEngineService;
import org.junit.Ignore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Ignore // not a test.
@SuppressForbidden(reason = "gradle is broken and tries to run me as a test")
public final class MessyTestUtils {
    public static ScriptServiceProxy getScriptServiceProxy(ThreadPool tp) throws Exception {
        Settings settings = Settings.settingsBuilder()
                .put("script.inline", "on")
                .put("script.indexed", "on")
                .put("path.home", LuceneTestCase.createTempDir())
                .build();
        XMustacheScriptEngineService mustacheScriptEngineService = new XMustacheScriptEngineService(settings);
        GroovyScriptEngineService groovyScriptEngineService = new GroovyScriptEngineService(settings);
        Set<ScriptEngineService> engineServiceSet = new HashSet<>();
        engineServiceSet.add(mustacheScriptEngineService);
        engineServiceSet.add(groovyScriptEngineService);
        ScriptContextRegistry registry = new ScriptContextRegistry(Arrays.asList(ScriptServiceProxy.INSTANCE));

        return  ScriptServiceProxy.of(new ScriptService(settings, new Environment(settings), engineServiceSet, new ResourceWatcherService(settings, tp), registry));
    }
}
