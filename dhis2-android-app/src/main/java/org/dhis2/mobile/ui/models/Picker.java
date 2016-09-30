/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.ui.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.dhis2.mobile.utils.Preconditions.checkNotNull;

/**
 * This class represents the node in tree data structure.
 */
public class Picker implements Serializable {
    private final String id;
    private final String name;
    private final String hint;
    private final boolean isPseudoRoot;

    // tag represents any object which we want to pass around
    private final Serializable tag;

    // parent node
    private final Picker parent;

    // list of filters which will be applied in UI
    private final List<Filter> filters;

    // available options (child nodes in tree)
    private final List<Picker> children;

    // selected item (represents path to selected node)
    private Picker selectedChild;

    private Picker(String id, String name, String hint, boolean isPseudoRoot, Serializable tag,
                   Picker parent) {
        this.id = id;
        this.name = name;
        this.hint = hint;
        this.isPseudoRoot = isPseudoRoot;
        this.tag = tag;

        // relationships
        this.parent = parent;
        this.filters = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    private Picker(String id, String name, String hint, boolean isPseudoRoot, Serializable tag,
                   Picker parent, List<Filter> filters, List<Picker> children, Picker selectedChild) {
        this.hint = hint;
        this.id = id;
        this.name = name;
        this.isPseudoRoot = isPseudoRoot;
        this.tag = tag;

        // relationships
        this.parent = parent;
        this.filters = filters;
        this.children = copyChildren(children);
        this.selectedChild = findSelectedChild(selectedChild);
    }

    // deep copy all nodes in subtree down to leaves
    private List<Picker> copyChildren(List<Picker> oldChildren) {
        List<Picker> newChildren = new ArrayList<>();

        if (oldChildren != null && !oldChildren.isEmpty()) {
            for (Picker child : oldChildren) {
                // recursively copying children with link to new parent
                newChildren.add(new Picker(
                        child.getId(), child.getName(), child.getHint(), child.isPseudoRoot(),
                        child.getTag(), this, child.getFilters(), child.getChildren(), child.getSelectedChild()));
            }
        }

        return newChildren;
    }

    // find corresponding selected child
    private Picker findSelectedChild(Picker selectedChild) {
        if (selectedChild == null) {
            return null;
        }

        for (Picker child : this.children) {
            if (selectedChild.equals(child)) {
                return child;
            }
        }

        return null;
    }

    public String getHint() {
        return hint;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Picker getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isPseudoRoot() {
        return isPseudoRoot;
    }

    public Serializable getTag() {
        return tag;
    }

    public boolean addFilter(Filter filter) {
        checkNotNull(filter, "Filter must not be null");

        return filters.add(filter);
    }

    public boolean addChild(Picker picker) {
        checkNotNull(picker, "Picker must not be null");

        if (children.isEmpty()) {
            return children.add(picker);
        }

        if (picker.isPseudoRoot() && areChildrenPseudoRoots()) {
            return children.add(picker);
        }

        if (!picker.isPseudoRoot() && !areChildrenPseudoRoots()) {
            return children.add(picker);
        }

        throw new IllegalArgumentException("All child nodes should be of the same " +
                "type (leaf or root)");
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public List<Picker> getChildren() {
        return children;
    }

    public Picker getSelectedChild() {
        return selectedChild;
    }

    public boolean areChildrenPseudoRoots() {
        for (Picker child : children) {
            if (!child.isPseudoRoot()) {
                return false;
            }
        }

        return true;
    }

    public void setSelectedChild(Picker selectedChild) {
        if (selectedChild != null && selectedChild.isPseudoRoot()) {
            throw new IllegalArgumentException("root picker cannot " +
                    "be set as selected child of given picker");
        }

        if (selectedChild != null && !equals(selectedChild.getParent())) {
            throw new IllegalArgumentException("Current instance of picker must be a " +
                    "parent of given child");
        }

        // if we set new selected child, we have to reset all descendants
        if (this.selectedChild != null) {
            this.selectedChild.setSelectedChild(null);
        } else {
            if (areChildrenPseudoRoots()) {
                // reset selection for adjacent trees as well
                for (Picker child : children) {
                    child.setSelectedChild(null);
                }
            }
        }

        this.selectedChild = selectedChild;
    }

    @Override
    public String toString() {
        return "Picker{" +
                "hint='" + hint + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", children=" + children +
                ", selectedChild=" + selectedChild +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Picker picker = (Picker) o;
        if (hint != null ? !hint.equals(picker.hint) : picker.hint != null) {
            return false;
        }

        if (id != null ? !id.equals(picker.id) : picker.id != null) {
            return false;
        }

        return name != null ? name.equals(picker.name) : picker.name == null;

    }

    @Override
    public int hashCode() {
        int result = hint != null ? hint.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public Builder buildUpon() {
        return new Builder(this);
    }

    public static class Builder {
        // source picker
        private Picker picker;

        // properties
        private String id;
        private String name;
        private String hint;
        private boolean isPseudoRoot;
        private Picker parent;
        private Serializable tag;

        private Builder(Picker picker) {
            checkNotNull(picker, "Picker must not be null");

            this.picker = picker;
            this.id = picker.getId();
            this.name = picker.getName();
            this.hint = picker.getHint();
            this.isPseudoRoot = picker.isPseudoRoot();
            this.tag = picker.getTag();
            this.parent = picker.getParent();
        }

        public Builder() {
            // empty constructor
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder hint(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder tag(Serializable tag) {
            this.tag = tag;
            return this;
        }

        public Builder parent(Picker parent) {
            this.parent = parent;
            return this;
        }

        public Builder asPseudoRoot() {
            this.isPseudoRoot = true;
            return this;
        }

        public Picker build() {
            if (picker != null) {
                return new Picker(id, name, hint, isPseudoRoot, tag,
                        parent, picker.getFilters(), picker.getChildren(), picker.getSelectedChild());
            }

            return new Picker(id, name, hint, isPseudoRoot, tag, parent);
        }
    }
}
