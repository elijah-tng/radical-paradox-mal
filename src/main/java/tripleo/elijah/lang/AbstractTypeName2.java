/*
 * Elijjah compiler, copyright Tripleo <oluoluolu+elijah@gmail.com>
 *
 * The contents of this library are released under the LGPL licence v3,
 * the GNU Lesser General Public License text was downloaded from
 * http://www.gnu.org/licenses/lgpl.html from `Version 3, 29 June 2007'
 *
 */
package tripleo.elijah.lang;

import java.util.Collection;
import java.util.LinkedHashSet;

/*
 * Created on 5/4/2019 at 12:25
 */
public abstract class AbstractTypeName2 implements NormalTypeName {

    // README/QUESTION  prob better with HashSet.
    //  tho by the time we get here, we should actually
    //  use ImmSet or UnmodifiableSet (Set.of)
    protected final Collection<TypeModifiers> _ltm = new LinkedHashSet<TypeModifiers>();
    protected Qualident typeName; // this should be EN_Name or whatever

    @Override
    public boolean isNull() {
        if (typeName == null) {
            return false;
        }
        // TODO check for correctness
        //  we'll do this next
        // idea says always false
        return _ltm.isEmpty() && typeName == null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final NormalTypeName that)) {
            return false;
        }
        return getConstant() == that.getConstant()
                && getReference() == that.getReference()
                && getOut() == that.getOut()
                && getIn() == that.getIn()
                // && type == that.type
                && getModifiers().containsAll(that.getModifiers())
                && getName().equals(that.getName());
    }

    @Override
    public boolean getConstant() {
        return _ltm.contains(TypeModifiers.CONST);
    }

    @Override
    public void setConstant(final boolean aFlag) {
        _ltm.add(TypeModifiers.CONST);
    }

    @Override
    public boolean getReference() {
        return _ltm.contains(TypeModifiers.REFPAR);
    }

    @Override
    public void setReference(final boolean aFlag) {
        _ltm.add(TypeModifiers.REFPAR);
    }

    // NOTE soon
    @Override
    public boolean getOut() {
        return _ltm.contains(TypeModifiers.OUTPAR);
    }

    @Override
    public void setOut(final boolean aFlag) {
        _ltm.add(TypeModifiers.OUTPAR);
    }

    @Override
    public boolean getIn() {
        return _ltm.contains(TypeModifiers.INPAR);
    }

    @Override
    public void setIn(final boolean aFlag) {
        _ltm.add(TypeModifiers.INPAR);
    }

    @Override
    public void setNullable() {
        _ltm.add(TypeModifiers.NULLABLE);
    }

    @Override
    public Collection<TypeModifiers> getModifiers() {
        return _ltm;
    }
}

//
//
//
