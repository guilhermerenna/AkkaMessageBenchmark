#!/bin/bash
if [ $# -ne 2 ]; then
	echo "faltando arquivo log ou parametro de saida.   delta ENTRADA SAIDA"

else 
	linhas_uteis=`cat $1 | grep -E 'Delta'`
	deltas=`echo $linhas_uteis | grep -E -o 'Delta.[[:blank:]][-0-9]{1,}' | grep -E -o '[-0-9]{1,}'`
	sents=`echo $linhas_uteis | grep -E -o 'Sent.[[:blank:]][-0-9]{1,}' | grep -E -o '[-0-9]{1,}'`	
	recebidos=`echo $linhas_uteis | grep -E -o 'Received.[[:blank:]][-0-9]{1,}' | grep -E -o '[-0-9]{1,}'`

	delta_total=0
	for delta in ${deltas[@]}
	do
		((delta_total += delta))
	done
	
	sent_total=0
	for sent in ${sents[@]}
	do
		((sent_total += sent))
	done

	recebidos_total=0
	for recebidos in ${recebidos}
	do
		((recebidos_total += recebidos))
	done


	
	linhas_uteis=`cat $1 | grep -E 'internal'`
	echo "$linhas_uteis"
	internals=`echo $linhas_uteis | grep -E -o 'internal.[[:blank:]][0-9]{1,}' | grep -E -o '[0-9]{1,}'`
	externals=`echo $linhas_uteis | grep -E -o 'external.[[:blank:]][0-9]{1,}' | grep -E -o '[0-9]{1,}'`
	internos_total=0
	for inter in ${internals[@]}
	do
		((internos_total += inter))
	done

	externos_total=0
	for ext in ${externals[@]}
	do
		((externos_total += ext))
	done

	d0=$((internos_total-sent_total))
	d1=$((externos_total-internos_total))
	d2=$((recebidos_total-externos_total))
	echo "$sent_total,$recebidos_total,$internos_total,$externos_total,$delta_total,$d0,$d1,$d2" >> $2

fi
