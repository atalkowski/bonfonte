package com.hack;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

public class BribesTest {

	private static String CHAOS = "Too chaotic";
	private static void log(String s, Object... objs) {
		StringBuilder sb = new StringBuilder();
		int objInx = 0;
		for(int inx = 0; inx < s.length(); inx++) {
			char c = s.charAt(inx);
			if(c == '{' && objInx < objs.length 
					&& inx + 1 < s.length() && s.charAt(inx+1) == '}'){
				inx++;
				Object obj = objs[objInx++];
				if(obj == null) {
					continue;
				}
				sb.append(obj.toString());
			}else {
				sb.append(c);
			}
		}
		System.out.println( sb.toString() );
	}
	
    //
    // Example : 
    /*
     * 
     *  5
		2 1  5  3  4
		1 0  2  0  0
		1 -1 2 -1 -1
		=> Answer 3 
		5
		2 5 1 3 4
		1 3 0 0 0
		=> Too chaotic .. 5 has reached a place that is too far forward!

		2 1  5 4 3
		1 0  2 0 0
		1 1  2 0 2
		=> Answer 4!!! 

		1 2 5 6 7 8 4 3
        0 0 2 2 2 2 1 0
		
		
		1 2 5 3 7 8 6 4
		Supposed to be 7?
		    2   2 2 1
     */
    static String minimumBribes2(int[] q) {
        int totalBribes = 0;
        for(int pos = 0; pos < q.length - 1; pos++){
            int bribes = q[pos] - pos - 1;
            if(bribes == 0) continue;
            if(bribes > 2){
                return "Too chaotic"; // Cannot move more than 2 paces forward!
            }
            if(bribes > 0) {
            	totalBribes += bribes;
            }else{
            	if(q[pos] > q[pos+1]) totalBribes ++;
            }
            
        }
        return totalBribes + "";
    }
    

    static String minimumBribes1(int[] q) {
        int totalBribes = 0;
        boolean slow = true;
        Map<Integer, Integer> processed = new HashMap<>();
//        long time = System.currentTimeMillis();
        for(int index = q.length-1; index >= 0; index--){
        	int pos = index + 1;
        	int value = q[index];
        	processed.put(value, 1);
            int bribes = q[index] - pos;
            if(bribes > 2){
                return "Too chaotic";
            }
            if(bribes <= 0){
                bribes = 0;
                if(slow){
	                for(int nextIndex = index+1; nextIndex < q.length; nextIndex++){
	                    if(q[index] > q[nextIndex]){
	                        bribes++;
	                        break;
	                    }
	                }
                }else{
                	for(int nextIndex = 1; nextIndex <= 7; nextIndex++){
                		if(value - nextIndex <= 0){
                			break;
                		}
                		Integer nextValue = processed.get(value - nextIndex); 
                		if(nextValue != null){
                			bribes++;
                			break;
                		}
                	}
                }
            }
           totalBribes += bribes;
        }
        //System.out.println(totalBribes + " in " + (System.currentTimeMillis() - time) + "ms");
        return totalBribes + "";
    }

    static int getSwaps(int[] q) {
    	int totalSwaps = 0;
    	int max = q.length - 1;
    	while(max > 1) {
        	int swaps = 0;
	    	for(int i = 0; i < max; i++) {
	    		if(q[i] > q[i+1]) {
	    			int t = q[i];
	    			q[i] = q[i+1];
	    			q[i+1] = t;
	    			swaps++;
	    		}
	    	}
	    	if(swaps == 0) break;
	    	max--;
	    	totalSwaps += swaps;
    	}
    	return totalSwaps;
    }

    static int minimumBribes(int[] q) {
        for(int pos = 0; pos < q.length - 1; pos++){
            int bribes = q[pos] - pos - 1;
            if(bribes > 2){
                return -1; // Cannot move more than 2 paces forward!
            }
        }
        return getSwaps(q);
    }
    
    // Complete the minimumBribes function below.
    static String minimumBribesVal(int[] q) {
    	int total = minimumBribes(q);
    	return total < 0 ? "Too chaotic" : total + "";
    }

    private String testRun(String input) {
    	InputStream stream = null;
    	String result = null;
    	try{
    		stream = new ByteArrayInputStream(input.getBytes("UTF-8"));
    	}catch(UnsupportedEncodingException e){
    		return result;
    	}
    	Scanner scanner = new Scanner(stream);
    	int t = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int tItr = 0; tItr < t; tItr++) {
            int n = scanner.nextInt();
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            int[] q = new int[n];

            String[] qItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int i = 0; i < n; i++) {
                int qItem = Integer.parseInt(qItems[i]);
                q[i] = qItem;
            }
            result = minimumBribesVal(q);
        }

        scanner.close();
        return result;
    }
    
    @Test 
    public void testBribe1(){
    	String sample = "1 5"
    			+ "\n5 1 2 3 4\n"
    			+ "";
    	Assert.assertEquals(CHAOS, testRun(sample));
    }
 
    @Test 
    public void testBribe2(){
    	String sample = "1 8"
    			+ "\n1 2 5 3 7 8 6 4\n"
    			+ "";
    	Assert.assertEquals(testRun(sample), "7");
    }
 
    @Test
    public void testBribe3(){
    	String sample = "1 1000\n"
    			+ "2 1 5 6 3 4 9 8 11 7 10 14 13 12 17 16 15 19 18 22 20 24 23 21 27 28 25 26 30 29 33 32 31 35 36 34 39 38 37 42 40 44 41 43 47 46 48 45 50 52 49 51 54 56 55 53 59 58 57 61 63 60 65 64 67 68 62 69 66 72 70 74 73 71 77 75 79 78 81 82 80 76 85 84 83 86 89 90 88 87 92 91 95 94 93 98 97 100 96 102 99 104 101 105 103 108 106 109 107 112 111 110 113 116 114 118 119 117 115 122 121 120 124 123 127 125 126 130 129 128 131 133 135 136 132 134 139 140 138 137 143 141 144 146 145 142 148 150 147 149 153 152 155 151 157 154 158 159 156 161 160 164 165 163 167 166 162 170 171 172 168 169 175 173 174 177 176 180 181 178 179 183 182 184 187 188 185 190 189 186 191 194 192 196 197 195 199 193 198 202 200 204 205 203 207 206 201 210 209 211 208 214 215 216 212 218 217 220 213 222 219 224 221 223 227 226 225 230 231 229 228 234 235 233 237 232 239 236 241 238 240 243 242 246 245 248 249 250 247 244 253 252 251 256 255 258 254 257 259 261 262 263 265 264 260 268 266 267 271 270 273 269 274 272 275 278 276 279 277 282 283 280 281 286 284 288 287 290 289 285 293 291 292 296 294 298 297 299 295 302 301 304 303 306 300 305 309 308 307 312 311 314 315 313 310 316 319 318 321 320 317 324 325 322 323 328 327 330 326 332 331 329 335 334 333 336 338 337 341 340 339 344 343 342 347 345 349 346 351 350 348 353 355 352 357 358 354 356 359 361 360 364 362 366 365 363 368 370 367 371 372 369 374 373 376 375 378 379 377 382 381 383 380 386 387 384 385 390 388 392 391 389 393 396 397 394 398 395 401 400 403 402 399 405 407 406 409 408 411 410 404 413 412 415 417 416 414 420 419 422 421 418 424 426 423 425 428 427 431 430 429 434 435 436 437 432 433 440 438 439 443 441 445 442 447 444 448 446 449 452 451 450 455 453 454 457 456 460 459 458 463 462 464 461 467 465 466 470 469 472 468 474 471 475 473 477 476 480 479 478 483 482 485 481 487 484 489 490 491 488 492 486 494 495 496 498 493 500 499 497 502 504 501 503 507 506 505 509 511 508 513 510 512 514 516 518 519 515 521 522 520 524 517 523 525 526 529 527 531 528 533 532 534 530 537 536 539 535 541 538 540 543 544 542 547 548 545 549 546 552 550 551 554 553 557 555 556 560 559 558 563 562 564 561 567 568 566 565 569 572 571 570 575 574 577 576 579 573 580 578 583 581 584 582 587 586 585 590 589 588 593 594 592 595 591 598 599 596 597 602 603 604 605 600 601 608 609 607 611 612 606 610 615 616 614 613 619 618 617 622 620 624 621 626 625 623 628 627 631 630 633 629 635 632 637 636 634 638 640 642 639 641 645 644 647 643 646 650 648 652 653 654 649 651 656 658 657 655 661 659 660 663 664 666 662 668 667 670 665 671 673 669 672 676 677 674 679 675 680 678 681 684 682 686 685 683 689 690 688 687 693 692 691 696 695 698 694 700 701 702 697 704 699 706 703 705 709 707 711 712 710 708 713 716 715 714 718 720 721 719 723 717 722 726 725 724 729 728 727 730 733 732 735 734 736 731 738 737 741 739 740 744 743 742 747 746 745 750 748 752 749 753 751 756 754 758 755 757 761 760 759 764 763 762 767 765 768 766 771 770 769 774 773 776 772 778 777 779 775 781 780 783 784 782 786 788 789 787 790 785 793 791 792 796 795 794 798 797 801 799 803 800 805 802 804 808 806 807 811 809 810 814 812 813 817 816 819 818 815 820 821 823 822 824 826 827 825 828 831 829 830 834 833 836 832 837 839 838 841 835 840 844 842 846 845 843 849 847 851 850 852 848 855 854 853 857 856 858 861 862 860 859 863 866 865 864 867 870 869 868 872 874 875 871 873 877 878 876 880 881 879 884 883 885 882 888 886 890 891 889 893 887 895 892 896 898 894 899 897 902 901 903 905 900 904 908 907 910 909 906 912 911 915 913 916 918 914 919 921 917 923 920 924 922 927 925 929 928 926 932 931 934 930 933 935 937 939 940 938 936 943 944 942 941 947 946 948 945 951 950 949 953 952 956 954 958 957 955 961 962 963 959 964 966 960 965 969 968 971 967 970 974 972 976 973 975 979 977 981 982 978 980 983 986 984 985 989 988 987 990 993 991 995 994 997 992 999 1000 996 998";
    	Assert.assertEquals(testRun(sample), "966");

    }

}
