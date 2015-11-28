
require 'json'

names = File.read('names.txt').split.each_slice(2).to_a

departments = File.read('departments.txt').lines.map(&:chomp)

employees = names.map do |fn, ln|
  {
    first_name: fn,
    last_name: ln,
    department: departments.sample,
    salary: rand(20)*100
  }
end

parts = []

5.times do
  parts << employees.shift(13 + rand(20))
end

parts << employees

puts parts.map(&:size)

json_opts = {
  indent: "  ",
  object_nl: "\n",
  array_nl: "\n",
  space: " ",
  space_before: " "
}

parts.each_with_index do |p, i|
  File.write("data-#{i+1}.json", JSON.generate(p, json_opts))
end
