package org.apache.lucene.search;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

/**
 * A remote searchable implementation.
 *
 * @version $Id: RemoteSearchable.java 351472 2005-12-01 21:15:53Z bmesser $
 */
@SuppressWarnings("serial")
public class RemoteSearchable extends UnicastRemoteObject implements Searchable {

    private Searchable local;

    /** Constructs and exports a remote searcher. */
    public RemoteSearchable(Searchable local) throws RemoteException {
        super();
        this.local = local;
    }

    // this implementation should be removed when the deprecated
    // Searchable#search(Query,Filter,HitCollector) is removed
    public void search(Query query, Filter filter, HitCollector results) throws IOException {
        local.search(query, filter, results);
    }

    public void search(Weight weight, Filter filter, HitCollector results) throws IOException {
        local.search(weight, filter, results);
    }

    public void close() throws IOException {
        local.close();
    }

    public int docFreq(Term term) throws IOException {
        return local.docFreq(term);
    }

    public int[] docFreqs(Term[] terms) throws IOException {
        return local.docFreqs(terms);
    }

    public int maxDoc() throws IOException {
        return local.maxDoc();
    }

    // this implementation should be removed when the deprecated
    // Searchable#search(Query,Filter,int) is removed
    public TopDocs search(Query query, Filter filter, int n) throws IOException {
        return local.search(query, filter, n);
    }

    public TopDocs search(Weight weight, Filter filter, int n) throws IOException {
        return local.search(weight, filter, n);
    }

    // this implementation should be removed when the deprecated
    // Searchable#search(Query,Filter,int,Sort) is removed
    public TopFieldDocs search(Query query, Filter filter, int n, Sort sort) throws IOException {
        return local.search(query, filter, n, sort);
    }

    public TopFieldDocs search(Weight weight, Filter filter, int n, Sort sort) throws IOException {
        return local.search(weight, filter, n, sort);
    }

    public Document doc(int i) throws IOException {
        return local.doc(i);
    }

    public Query rewrite(Query original) throws IOException {
        return local.rewrite(original);
    }

    // this implementation should be removed when the deprecated
    // Searchable#explain(Query,int) is removed
    public Explanation explain(Query query, int doc) throws IOException {
        return local.explain(query, doc);
    }

    public Explanation explain(Weight weight, int doc) throws IOException {
        return local.explain(weight, doc);
    }

    /** Exports a searcher for the index in args[0] named
     * "//localhost/Searchable". */
    public static void main(String args[]) throws Exception {
        String indexName = null;

        if (args != null && args.length == 1)
            indexName = args[0];

        if (indexName == null) {
            System.out.println("Usage: org.apache.lucene.search.RemoteSearchable <index>");
            return;
        }

        // create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        Searchable local = new IndexSearcher(indexName);
        RemoteSearchable impl = new RemoteSearchable(local);

        // bind the implementation to "Searchable"
        Naming.rebind("//localhost/Searchable", impl);
    }

}
