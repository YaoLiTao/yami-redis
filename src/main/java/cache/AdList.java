package cache;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public class AdList<T> {

    @Setter
    private ListNode<T> head;

    @Setter
    private ListNode<T> tail;

    private long len;

    @Setter
    private Consumer<T> dup;

    @Setter
    private Consumer<T> free;

    @Setter
    private BiFunction<T, T, Boolean> match;

    /**
     * 添加节点
     */
    private void listAddNode(T value, boolean head) {
        ListNode<T> newNode = new ListNode<>();
        newNode.value = value;
        if (len == 0) {
            this.head = newNode;
            this.tail = newNode;
            len++;
            return;
        }
        if (head) {
            this.head.prev = newNode;
            newNode.next = this.head;
            this.head = newNode;
        } else {
            this.tail.next = newNode;
            newNode.prev = this.tail;
            this.tail = newNode;
        }
        len++;
    }

    /**
     * 添加节点到头部
     */
    public void listAddNodeHead(T value) {
        listAddNode(value, true);
    }

    /**
     * 添加节点到尾部
     */
    public void listAddNodeTail(T value) {
        listAddNode(value, false);
    }

    /**
     * 添加节点到某个节点的前/后面
     */
    public void listInsertNode(ListNode<T> oldNode, T value, boolean after) {
        ListNode<T> newNode = new ListNode<>();
        newNode.value = value;
        if (after) {
             newNode.prev = oldNode;
            newNode.next = oldNode.next;
            if (this.tail == oldNode) {
                this.tail = newNode;
            }
        } else {
            newNode.next = oldNode;
            newNode.prev = oldNode.prev;
            if (this.head == oldNode) {
                this.head = newNode;
            }
        }
        if (Objects.nonNull(newNode.prev)) {
            newNode.prev.next = newNode;
        }
        if (Objects.nonNull(newNode.next)) {
            newNode.next.prev = newNode;
        }
        len++;
    }

    /**
     * 通过值搜索节点
     */
    public ListNode<T> listSearchKey(T value) {
        ListNode<T> curNode = head;
        while (Objects.nonNull(curNode)) {
            if (Objects.nonNull(match)) {
                if (match.apply(curNode.value, value)) {
                    return curNode;
                }
            } else {
                if (value.equals(curNode.value)) {
                    return curNode;
                }
            }
            curNode = curNode.next;
        }
        return null;
    }

    /**
     * 通过位置获取节点
     */
    public ListNode<T> listIndex(long index) {
        ListNode<T> tempNode;

        if (index < 0) {
            index = (-index) - 1;
            tempNode = this.tail;
            while ((index--) > 0 && Objects.nonNull(tempNode)) {
                tempNode = tempNode.prev;
            }
        } else {
            tempNode = this.head;
            while ((index--) > 0 && Objects.nonNull(tempNode)) {
                tempNode = tempNode.next;
            }
        }
        return tempNode;
    }

    /**
     * 删除节点
     */
    public void listDelNode(ListNode<T> node) {
        if (Objects.nonNull(node.prev)) {
            node.prev.next = node.next;
        } else {
            this.head = node.next;
        }
        if (Objects.nonNull(node.next)) {
            node.next.prev = node.prev;
        } else {
            this.tail = node.prev;
        }
        len--;
    }
}
