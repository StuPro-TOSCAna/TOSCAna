#!/usr/bin/env fish
for i in dot-src/*.dot
    set outpath (echo $i | sed 's|.dot|.png|g;s|dot-src/||g')
    echo "$i -> $outpath"
    dot $i -Tpng -o $outpath
end
