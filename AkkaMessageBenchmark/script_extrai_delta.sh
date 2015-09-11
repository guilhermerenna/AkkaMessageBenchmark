#!/bin/bash
if [ $# -ne 1 ]; then
	echo "faltando arquivo log.   script_extrai_delta ARQUIVO"

else 
	valores=`cat $1 | grep -E -o 'Delta.[[:blank:]].[-0-9]{1,}' | grep -E -o '[-0-9]{1,}'`
	delta_total=0
	for delta in ${valores[@]}
	do
	((delta_total += delta))
	done
	echo "delta total: $delta_total"	
fi
