

#@files = glob(); 
opendir(my $directory_handle, "/path/to/directory/") or die "Unable to open directory: $!";
while (my $file_name = <$directory_handle>) {
  #next if $file_name =~ /some_pattern/; # Skip files matching pattern
  $suffix = substr($file_name, index($file_name, '\.'), length($file_name));
  'cat  submissions/*.$suffix > finalTriple.$suffix'; 
  #open (my $file_handle, '>', $file_name) or warn "Could not open file '$file_name': $!";
  # Write something to $file_name. See <code>perldoc -f open</code>.
  #close $file_handle;
}
closedir $directory_handle;

#for subdir in submissions/*; {
#	do position=$((${#subdir}-2)); 
#	echo ${subdir}|cut -c${position}-${#subdir};
#	done
#}
