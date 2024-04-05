package com.hack;

public class Atlassian {

	
	static class SinglyLinkedListNode {
	    int data;
	    SinglyLinkedListNode next;
	    public SinglyLinkedListNode(int data) {
	    	this.data = data;
	    }
	}
	
	static SinglyLinkedListNode reverse(SinglyLinkedListNode head) {
		if(head == null || head.next == null) return head;
		SinglyLinkedListNode rev = null;
		while(head != null) {
			SinglyLinkedListNode cur = rev;
			rev = new SinglyLinkedListNode(head.data);
			rev.next = cur;
			head = head.next;
		}
		return rev;
    }

    static boolean compareLists(SinglyLinkedListNode head1, SinglyLinkedListNode head2) {
        while(head1 != null && head2 != null){
            if(head1.data != head2.data) return false;
            head1 = head1.next;
            head2 = head2.next;
        }
        return head1 == null && head2 == null;
    }

    static void append(SinglyLinkedListNode head1, SinglyLinkedListNode head2){
        if(head1 == null || head2 == null) return; // Nothing to do here!
        while(head1.next != null) head1 = head1.next;
        while(head2 != null){
            SinglyLinkedListNode next = new SinglyLinkedListNode(head2.data);
            head1.next = next;
            head1 = next;
            head2 = head2.next;
        }
    }
    
    static SinglyLinkedListNode removeDuplicates(SinglyLinkedListNode head) {
        SinglyLinkedListNode parent = head;
        while(parent != null){
            SinglyLinkedListNode next = parent.next;
            if(next == null) break;
            if(next.data == parent.data){
                parent.next = next.next;
            }else{
                parent = next;
            }
        }
        return head;
    }

    static SinglyLinkedListNode mergeLists(SinglyLinkedListNode head1, SinglyLinkedListNode head2) {
       if(head1 == null && head2 == null) return null;
       SinglyLinkedListNode res = null;
       SinglyLinkedListNode cur = null;

       while(head1 != null && head2 != null){
           SinglyLinkedListNode parent = cur;
           if(head1.data <= head2.data){
               cur = new SinglyLinkedListNode(head1.data);
               head1 = head1.next;
           }else{
               cur = new SinglyLinkedListNode(head2.data);
               head2 = head2.next;
           }
           if(parent == null){
               res = cur;
           }else{
               parent.next = cur;
               parent = cur;
           }
       }
       append(cur, head1);
       append(cur, head2);
       return res;
   }

}

