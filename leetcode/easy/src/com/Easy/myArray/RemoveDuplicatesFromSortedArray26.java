package com.Easy.myArray;


public class RemoveDuplicatesFromSortedArray26 {
    public static int removeDuplicates1(int[] nums){
        int count = 0;
        int temp = nums[nums.length-1];
        for(int i = nums.length-1,j = nums.length-1;i>=0;){
            if(nums[j] != nums[i]){
                i--;
            }
        }

        return count;
    }





















    public static void main(String[] args) {

    }

    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        //快慢针解法，i在j前面一位来进行处理,
        //和27的不同在于他是删除重复而不是选定值，所以快针会在慢针前面一位
        int i = 0;
        for (int j = 1; j < nums.length; j++) {
            if (nums[i] != nums[j]) {
                //和27不同是，慢针要进一步再赋值，和27正好相反
                i++;
                nums[i] = nums[j];
            }
        }
        return i + 1;
    }
}
