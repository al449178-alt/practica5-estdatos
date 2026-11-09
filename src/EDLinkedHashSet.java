import java.util.*;

public class EDLinkedHashSet<T> implements Set<T> {

    private class Node {
        T data;
        Node hnext = null;   // siguiente nodo en la cadena de la cubeta de la tabla
        Node prev = null;    // anterior en la lista de orden de inserción
        Node next = null;    // siguiente en la lista de orden de inserción
    }

    static final private int DEFAULT_CAPACITY = 11;
    static final private double DEFAULT_LOAD_FACTOR = 2.0;

    Node[] table;
    Node header;
    int size = 0;
    int rehashThreshold;
    double loadFactor;

    // ------------------------------------------------------------------
    // Métodos que se dan implementados
    // ------------------------------------------------------------------

    public EDLinkedHashSet() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /** Posición de la tabla en la que dispersa item (admite null). */
    private int hash(Object item) {
        if (item == null)
            return 0;
        return (item.hashCode() & Integer.MAX_VALUE) % table.length;
    }

    /** Compara dos elementos teniendo en cuenta que alguno puede ser null. */
    private boolean equalsNull(Object a, Object b) {
        if (a == null)
            return b == null;
        return a.equals(b);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /** Recorre directamente la lista de orden desde el nodo cabecera. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node n = header.next;
        while (n != header) {
            sb.append(n.data);
            n = n.next;
            if (n != header)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (T e : this)
            result[i++] = e;
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        if (a.length < size)
            a = (E[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        for (T e : this)
            a[i++] = (E) e;
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T e : c)
            if (add(e))
                changed = true;
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object e : c)
            if (remove(e))
                changed = true;
        return changed;
    }

    // ------------------------------------------------------------------
    // Ejercicio 1
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public EDLinkedHashSet(int capacity, double loadFactor) {
        //TODO Ejercicio 1
        if (capacity <= 0 || loadFactor <=0) throw new IllegalArgumentException();

        this.table = new EDLinkedHashSet.Node[capacity];
        this.loadFactor = loadFactor;
        this.rehashThreshold = (int) (capacity * loadFactor);
        this.header = new Node();
        this.header.next = this.header;
        this.header.prev = this.header;
        //la tabla se crea con new EDLinkedHashSet.Node[tamaño]
    }

    // ------------------------------------------------------------------
    // Ejercicio 2
    // ------------------------------------------------------------------

    private void linkLast(Node n) {
        //TODO Ejercicio 2
        n.prev=header.prev;
        header.prev.next=n;
        n.next=header;
        header.prev=n;
    }

    private void unlink(Node n) {
        //TODO Ejercicio 2
        n.prev.next=n.next;
        n.next.prev=n.prev;
    }

    // ------------------------------------------------------------------
    // Ejercicio 3
    // ------------------------------------------------------------------

    private Node findNode(Object item) {
        //TODO Ejercicio 3
        int celda= hash(item);
        Node aux = table[celda];
        while(aux!=null){
            if (equalsNull(aux.data, item)) return aux;
            aux=aux.hnext;
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Ejercicio 4
    // ------------------------------------------------------------------

    @Override
    public boolean contains(Object item) {
        //TODO Ejercicio 4
        return findNode(item)!=null;
    }

    // ------------------------------------------------------------------
    // Ejercicio 5
    // ------------------------------------------------------------------

    @Override
    public boolean add(T item) {
        //TODO Ejercicio 5
        if (contains(item)) return false;

        Node n = new Node();
        n.data= item;

        linkLast(n);
        int index=hash(item);
        n.hnext=table[index];
        table[index]=n;

        size++;
        if (size>rehashThreshold) rehash();
        return true;
    }

    // ------------------------------------------------------------------
    // Ejercicio 6
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private void rehash() {
        //TODO Ejercicio 6
        int newCapacity = table.length*2+1;
        Node[] tableaux = new EDLinkedHashSet.Node[newCapacity];
        table = tableaux;
        rehashThreshold = (int) (newCapacity*loadFactor);

        Node aux = header.next;
        while(aux!=header){
            int index = hash(aux.data);
            aux.hnext =table[index];
            table[index] = aux;
            aux = aux.next;
        }
    }

    // ------------------------------------------------------------------
    // Ejercicio 7
    // ------------------------------------------------------------------

    @Override
    public boolean remove(Object item) {
        //TODO Ejercicio 7
        int index = hash(item);
        Node aux= table[index];
        Node prev= null;
        while (aux!=null){
            if (equalsNull(aux.data, item)){
                if(prev==null){
                    table[index]=aux.hnext;
                } else {
                    prev.hnext=aux.hnext;
                }
                unlink(aux);
                size--;
                return true;
            }
            prev= aux;
            aux=aux.hnext;
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Ejercicio 8
    // ------------------------------------------------------------------

    @Override
    public void clear() {
        //TODO Ejercicio 8
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        header.next = header;
        header.prev = header;
        size = 0;
    }

    // ------------------------------------------------------------------
    // Ejercicio 9
    // ------------------------------------------------------------------

    private class EDIterator implements Iterator<T> {
        Node current = header.next;   // nodo que devolverá next
        Node lastReturned = null;     // último nodo devuelto por next

        @Override
        public boolean hasNext() {
            //TODO Ejercicio 9
            return current != header;
        }

        @Override
        public T next() {
            //TODO Ejercicio 9
            if (!hasNext()) throw new NoSuchElementException();
            lastReturned=current;
            T data = current.data;
            current=current.next;
            return data;
            //Debe actualizar lastReturned para que lo use remove
        }

        @Override
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            EDLinkedHashSet.this.remove(lastReturned.data);
            lastReturned = null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new EDIterator();
    }

    // ------------------------------------------------------------------
    // Ejercicio 10
    // ------------------------------------------------------------------

    @Override
    public boolean containsAll(Collection<?> c) {
        //TODO Ejercicio 10
        for (Object i : c){
            if (!contains(i)) return false;
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Ejercicio 11
    // ------------------------------------------------------------------
    @Override
    public boolean retainAll(Collection<?> c) {
        //TODO Ejercicio 11
        boolean eliminado = false;
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                eliminado = true;
            }
        }
        return eliminado;
    }
}
