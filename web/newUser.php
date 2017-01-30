<?php
require_once 'functions.php';
$link=sqlcon();

	$data=strip($link);
	$operand=array("Delay");
if(missingOperand($operand,$_POST)){
		$data=strip($link);
	
	$cname=$data['Delay'];
	
	$json_data = file_get_contents('words.json');
	$data =json_decode($json_data, true);
	$data=$data["data"];
	$word1=$data[array_rand($data)];
	$word2=$data[array_rand($data)];
	$word3=$data[array_rand($data)];
	
	$name = $word1." ".$word2." ".$word3;
	
	$time=time();
	
	$query="INSERT INTO bedmove
		(Name,Time,Delay)
		VALUES 
		('$name','$time','$cname');";
		$result=mysqli_query($link, $query) 
		or die(mysqli_error($link));
		
	$reultrs = array("sucsess"=>true,"name" => $name);
}else $reultrs = array("sucsess"=>false);
	
echo json_encode($reultrs);

?>